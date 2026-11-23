# Demonstração da integração entre cliente e servidor

Este roteiro demonstra o cadastro, a autenticação com JWT, o registro de uma partida rápida e a consulta ao ranking global da Fase 8.

Partidas da campanha não são enviadas ao servidor. Essa é uma decisão intencional da Fase 8: somente partidas rápidas participam dos rankings local ou global.

## 1. Pré-requisitos

- JDK 17 ativo.
- OpenSSL disponível no terminal do macOS.
- Portas locais livres, principalmente a porta `8080`.
- Dois terminais: um para o servidor e outro para o cliente.

Confira o Java:

```bash
java -version
```

## 2. Iniciar o servidor

No primeiro terminal, a partir da raiz do repositório:

```bash
cd fases/fase-08-campanha-fundadores/servidor
export MISSAOMARTE_JWT_SEGREDO=$(openssl rand -base64 48)
./gradlew bootRun
```

O comando do OpenSSL gera um segredo novo e o mantém somente na variável de ambiente do terminal. Cada vez que a geração for executada, o valor será diferente. Tokens emitidos com o valor anterior deixarão de ser aceitos, portanto logins feitos antes da troca precisarão ser repetidos.

Configuração padrão:

- Endereço de escuta: `127.0.0.1`.
- Porta: `8080`.
- URL base: `http://localhost:8080`.
- Swagger UI: `http://localhost:8080/swagger-ui.html`.
- Documento OpenAPI: `http://localhost:8080/v3/api-docs`.

Confirme que o servidor está funcionando:

```bash
curl http://localhost:8080/api/saude
```

Resposta esperada:

```json
{
  "status": "ok"
}
```

## 3. Demonstrar a API pelo Swagger

Abra `http://localhost:8080/swagger-ui.html` no navegador. As operações de cadastro e login são públicas. As operações de partida, histórico e ranking exigem autenticação.

### 3.1 Cadastrar um usuário

Abra `POST /api/usuarios`, selecione `Try it out` e use um nome ainda não cadastrado:

```json
{
  "nome": "piloto_demo",
  "senha": "<senha-temporaria-com-8-ou-mais-caracteres>"
}
```

Execute a chamada. O resultado esperado é `201 Created`, com uma resposta semelhante a:

```json
{
  "id": 1,
  "nome": "piloto_demo"
}
```

O nome aceita de 3 a 20 letras, números, `_` ou `-`. A senha precisa ter pelo menos 8 caracteres. Se o nome já existir, o servidor responderá `409 Conflict`.

### 3.2 Fazer login

Abra `POST /api/auth/login` e informe as mesmas credenciais:

```json
{
  "nome": "piloto_demo",
  "senha": "<a-mesma-senha-temporaria>"
}
```

O resultado esperado é `200 OK`. A resposta contém um token:

```json
{
  "token": "<token-jwt>",
  "tipo": "Bearer",
  "expiraEm": "<data-e-hora-em-UTC>"
}
```

Copie o valor do campo `token`, abra o botão `Authorize` do Swagger e cole somente o token, sem escrever a palavra `Bearer`. Não registre o token em arquivos, capturas ou commits.

### 3.3 Registrar uma partida

Abra `POST /api/partidas`. O Swagger enviará o token autorizado no cabeçalho. Use:

```json
{
  "dificuldade": "FACIL",
  "resultado": "VITORIA",
  "pontuacao": 120,
  "duracaoSegundos": 45,
  "movimentos": 30
}
```

O resultado esperado é `201 Created`. O usuário não aparece no corpo: o servidor identifica o dono da partida pelo JWT.

Valores técnicos aceitos:

- `dificuldade`: `FACIL`, `MEDIO` ou `DIFICIL`.
- `resultado`: `VITORIA` ou `DERROTA`.

### 3.4 Consultar o ranking

Abra `GET /api/ranking` e execute a chamada autorizada. Esse endpoint não possui corpo de requisição.

O resultado esperado é `200 OK`, com uma lista semelhante a:

```json
[
  {
    "nome": "piloto_demo",
    "pontuacao": 120,
    "duracaoSegundos": 45,
    "movimentos": 30,
    "dificuldade": "FACIL",
    "jogadaEm": "<data-e-hora-em-UTC>"
  }
]
```

O ranking apresenta no máximo dez jogadores. Para cada jogador, considera somente a melhor vitória com pontuação positiva.

## 4. Demonstrar pelo cliente desktop

Mantenha o servidor aberto. No segundo terminal, a partir da raiz do repositório:

```bash
cd fases/fase-08-campanha-fundadores/cliente
./gradlew :lwjgl3:run -Dmissaomarte.servidor=http://localhost:8080
```

O desktop já usa `http://localhost:8080` como padrão. A propriedade do comando apenas torna o endereço explícito durante a demonstração.

### 4.1 Conferir a URL do servidor

1. No menu principal, abra `Opções`.
2. Abra `Servidor`.
3. Confira o endereço `localhost`, a porta `8080` e HTTPS desativado.
4. Use `Testar conexão`. A tela deve mostrar `Conexão com o servidor OK.`.
5. Use `Salvar` e volte para `Opções`.

O cliente resolve o endereço nesta ordem:

1. Configuração salva pelo jogador nas preferências.
2. Endereço padrão informado pelo inicializador da plataforma.
3. No desktop, `-Dmissaomarte.servidor`, cujo padrão é `http://localhost:8080`.
4. No Android, o valor de build `-PservidorPadrao`, também substituível pela tela `Servidor`.

A configuração salva contém somente host, porta e uso de HTTPS. Senha e token não são gravados nela. Se o servidor for trocado durante uma sessão, o cliente encerra o login atual.

### 4.2 Criar a conta ou entrar

1. Em `Opções`, abra `Conta`.
2. Para uma conta nova, use `Criar conta` ou pressione `F2`.
3. Preencha nome e senha e confirme com `Enter`.
4. Depois de um cadastro bem-sucedido, o cliente faz o login automaticamente.
5. Para uma conta existente, mantenha o modo `Entrar`, preencha as credenciais e confirme.

Depois do login, o menu mostra o nome do jogador e a indicação `ranking global`. A sessão e o token permanecem somente em memória e são descartados ao sair da conta, trocar de servidor, receber uma resposta `401` ou fechar o jogo.

### 4.3 Enviar um resultado real do jogo

1. No menu principal, selecione `Partida rápida`.
2. Conclua a partida com vitória ou derrota.
3. Na tela de fim, confira `Enviando o resultado ao servidor...` e depois `Resultado enviado ao servidor.`.
4. Volte ao menu e abra `Ranking`.
5. Confira o `RANKING GLOBAL - TOP 10` carregado do servidor.

Não use a campanha nesta etapa da demonstração. Resultados da campanha ficam apenas no progresso local e não entram em nenhum ranking.

## 5. Comportamento com o servidor desligado

As chamadas HTTP do cliente têm limite de 5 segundos.

- `Testar conexão`, cadastro e login mostram `Não foi possível conectar ao servidor (...)`.
- Sem uma sessão, o jogo continua funcionando como convidado e usa o ranking local.
- Se o servidor cair depois do login, a consulta ao ranking global mostra o erro e permite tentar novamente.
- Se o envio de uma vitória em partida rápida falhar por falta de conexão, o cliente a registra uma vez no ranking local com o nome do jogador e mostra `Sem conexão: salvo no ranking local`.
- No desktop, enquanto a tela final ainda estiver aberta e a sessão continuar válida, a tecla `R` tenta reenviar o resultado. Um envio posterior bem-sucedido não cria outra entrada no ranking local.
- Não existe fila persistente de resultados pendentes para envio ao servidor.
- Uma falha de conexão não encerra a sessão. Uma resposta `401` encerra o login porque indica token ausente, inválido ou expirado.
- A campanha continua jogável, pois seu progresso e sua pontuação são locais.

Para demonstrar esse comportamento, pare o servidor com `Ctrl+C`, tente abrir o ranking global ou fazer login e observe a mensagem. Depois, reinicie o servidor e faça um novo login caso o segredo JWT tenha sido gerado novamente.

## 6. Banco H2

O servidor usa H2 em arquivo. Por padrão, os dados ficam em `fases/fase-08-campanha-fundadores/servidor/dados/missaomarte`. A variável `MISSAOMARTE_DADOS` pode indicar outra pasta.

Não existe console web do H2 habilitado nesta fase:

- `spring.h2.console.enabled` não está ativo.
- A rota `/h2-console` não está liberada pela configuração de segurança.
- Portanto, não há uma URL de console H2 para acessar durante a demonstração.

Os dados necessários podem ser demonstrados pelas operações de histórico e ranking da própria API.

## 7. Pendências para o Android

Na configuração atual, o servidor escuta apenas em `127.0.0.1`. Esse endereço aceita conexões somente da própria máquina. Um celular físico, mesmo conectado à mesma rede Wi-Fi, não consegue acessar esse servidor.

Para permitir uma demonstração futura em celular físico, será necessário:

1. Iniciar o servidor com `SERVER_ADDRESS=0.0.0.0`, para aceitar conexões pelas interfaces de rede da máquina.
2. Descobrir o IP local do Mac na mesma rede do celular, por exemplo com `ipconfig getifaddr en0` quando o Wi-Fi estiver nessa interface.
3. Configurar no jogo o IP encontrado, a porta `8080` e HTTP para o teste local. Exemplo de endereço: `http://192.168.0.10:8080`.
4. Confirmar que computador e celular estão na mesma rede e que o firewall permite conexões de entrada na porta `8080`.
5. Usar o APK de debug, que permite HTTP para testes locais. Uma distribuição real deve usar HTTPS.

Para o emulador Android, o endereço especial do computador hospedeiro é `10.0.2.2`, mas ele não resolve o acesso de um celular físico.

Nenhuma dessas mudanças está ativa neste roteiro. A demonstração atual usa o cliente desktop e mantém o servidor restrito a `127.0.0.1`.

## 8. Encerrar a demonstração

1. Feche o cliente.
2. No terminal do servidor, pressione `Ctrl+C`.
3. Remova o segredo do ambiente do terminal:

```bash
unset MISSAOMARTE_JWT_SEGREDO
```

O banco H2 permanece no disco para a próxima execução. Se um novo segredo for gerado ao reiniciar, os usuários e partidas continuam no banco, mas será necessário fazer login novamente para receber um token novo.
