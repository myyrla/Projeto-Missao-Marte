# Publicação do servidor da Fase 8

Este roteiro explica como empacotar, executar e publicar o servidor de ranking da Fase 8. Ele considera o ambiente da
equipe, com macOS, terminal `zsh` e Java 17.

O servidor é acadêmico. Ele não tem limitação de tentativas de login, monitoramento de produção ou validação de que a
pontuação enviada pelo cliente é legítima. Use contas de teste e não mantenha uma instância pública sem supervisão.

## 1. Pré-requisitos

- JDK 17 ativo.
- OpenSSL para gerar o segredo do JWT.
- Uma porta livre para o processo Java, normalmente `8080`.
- Um diretório persistente e gravável para o banco H2.
- Para publicação na internet: domínio, certificado TLS válido, proxy reverso e firewall.

Confira o Java:

```bash
java -version
```

O resultado deve indicar Java 17.

## 2. Gerar o pacote executável

A partir da raiz do repositório:

```bash
cd fases/fase-08-campanha-fundadores/servidor
./gradlew clean test bootJar
```

O `build.gradle` fixa o nome do pacote executável. O arquivo gerado é:

```text
build/libs/app.jar
```

Confira sua existência:

```bash
ls -lh build/libs/app.jar
```

O JAR já contém o servidor HTTP e as dependências da aplicação. Não é necessário instalar Tomcat separadamente.

## 3. Variáveis de ambiente

| Variável | Padrão | Exemplo de publicação | Finalidade |
|---|---:|---|---|
| `MISSAOMARTE_JWT_SEGREDO` | vazio | valor gerado fora do repositório | Assina os tokens JWT com HS256. Precisa ter pelo menos 32 bytes. |
| `MISSAOMARTE_DADOS` | `./dados` | `/var/lib/missaomarte` | Diretório do banco H2 em arquivo. |
| `PORT` | `8080` | `8080` | Porta HTTP interna do processo Java. |
| `SERVER_ADDRESS` | `127.0.0.1` | `127.0.0.1` ou `0.0.0.0` | Endereço de escuta do servidor. |
| `SPRINGDOC_SWAGGERUI_ENABLED` | `true` | `false` | Liga ou desliga a interface Swagger. |
| `SPRINGDOC_APIDOCS_ENABLED` | `true` | `false` | Liga ou desliga o documento OpenAPI em `/v3/api-docs`. |

As duas últimas variáveis são a forma de ambiente das propriedades `springdoc.swagger-ui.enabled` e
`springdoc.api-docs.enabled`. O Spring Boot converte pontos em `_`, remove hífens e usa letras maiúsculas.

### 3.1 Segredo do JWT

Gere um segredo forte sem escrevê-lo no código ou em um arquivo versionado:

```bash
export MISSAOMARTE_JWT_SEGREDO="$(openssl rand -base64 48)"
```

O comando gera um valor diferente a cada execução. Em produção, gere uma vez e cadastre o valor no gerenciador de
segredos da hospedagem. O mesmo valor deve ser injetado em todas as reinicializações. Se ele mudar, os usuários e as
partidas continuam no banco, mas todos os tokens emitidos anteriormente deixam de valer e os jogadores precisam entrar
novamente.

Não exiba o valor no terminal, não o coloque em capturas e não o grave no repositório.

### 3.2 Diretório de dados

Use um caminho absoluto em produção:

```bash
export MISSAOMARTE_DADOS=/var/lib/missaomarte
```

O padrão `./dados` é relativo ao diretório em que `java -jar` foi iniciado, não ao local do JAR. Um caminho absoluto evita
criar bancos diferentes por engano quando o diretório de trabalho mudar.

O usuário que executa o servidor precisa conseguir criar e alterar arquivos nesse diretório. Restrinja o acesso ao mesmo
usuário do processo sempre que o sistema operacional permitir.

## 4. Executar com `java -jar`

### 4.1 Teste apenas na própria máquina

Em um terminal:

```bash
export MISSAOMARTE_JWT_SEGREDO="$(openssl rand -base64 48)"
export MISSAOMARTE_DADOS="$PWD/dados"
export SERVER_ADDRESS=127.0.0.1
export PORT=8080
java -jar build/libs/app.jar
```

Em outro terminal:

```bash
curl http://localhost:8080/api/saude
```

Resposta esperada:

```json
{"status":"ok"}
```

Encerre o servidor com `Ctrl+C`. O encerramento gracioso permite que o Spring feche as conexões antes de terminar o
processo.

### 4.2 Configuração recomendada atrás de proxy reverso

Quando o proxy estiver na mesma máquina, mantenha a aplicação inacessível diretamente pela internet:

```bash
export MISSAOMARTE_DADOS=/var/lib/missaomarte
export SERVER_ADDRESS=127.0.0.1
export PORT=8080
export SPRINGDOC_SWAGGERUI_ENABLED=false
export SPRINGDOC_APIDOCS_ENABLED=false
java -jar build/libs/app.jar
```

O gerenciador de processos ou serviço do sistema deve injetar também `MISSAOMARTE_JWT_SEGREDO` a partir de um local
seguro. Não coloque o segredo no arquivo do serviço se esse arquivo for público ou versionado.

## 5. Tornar o servidor acessível na rede local

Para um celular físico na mesma rede acessar diretamente o servidor:

1. Inicie com `SERVER_ADDRESS=0.0.0.0`.
2. Descubra o endereço IP do computador na rede.
3. Libere a porta somente para a rede privada ou para a sub-rede necessária.
4. Configure o cliente com o IP, a porta e HTTP.
5. Feche a porta quando a demonstração terminar.

No macOS, o IP do Wi-Fi normalmente pode ser obtido com:

```bash
ipconfig getifaddr en0
```

Exemplo de inicialização:

```bash
export MISSAOMARTE_JWT_SEGREDO="$(openssl rand -base64 48)"
export MISSAOMARTE_DADOS="$PWD/dados"
export SERVER_ADDRESS=0.0.0.0
export PORT=8080
java -jar build/libs/app.jar
```

Teste de outro computador na mesma rede:

```bash
curl http://192.168.0.10:8080/api/saude
```

Substitua `192.168.0.10` pelo IP real. Autorize conexões de entrada para o processo Java somente na rede privada. Em um
firewall de nuvem ou roteador, limite a origem à sub-rede confiável em vez de liberar a porta para toda a internet.

HTTP envia senha e token sem criptografia. Use esse modo apenas para uma demonstração em rede confiável e com o APK de
debug. O Android release desta fase bloqueia tráfego HTTP sem criptografia.

## 6. Publicar na internet com HTTPS

Não exponha o servidor Java diretamente. Use esta topologia:

```text
Cliente HTTPS
    |
    v
Proxy reverso: portas públicas 443 e, opcionalmente, 80
    |
    v
Servidor Java: 127.0.0.1:8080
```

Passos:

1. Aponte o DNS do domínio para a máquina ou para o serviço de nuvem.
2. Obtenha um certificado TLS válido pelo provedor ou por uma autoridade como Let's Encrypt.
3. Execute o Java em `127.0.0.1:8080` quando o proxy estiver na mesma máquina.
4. Configure o proxy para encaminhar a raiz do domínio para `http://127.0.0.1:8080`.
5. Faça o proxy substituir os cabeçalhos encaminhados. Não confie em valores `X-Forwarded-*` enviados diretamente pelo
   cliente público.
6. No firewall externo, permita `443` e, se houver redirecionamento, `80`. Não libere `8080` para a internet.
7. Confirme o acesso por `https://api.exemplo.com/api/saude`.

O servidor já usa `server.forward-headers-strategy=framework`, portanto interpreta os cabeçalhos do proxy ao gerar URLs e
redirecionamentos.

### 6.1 Exemplo mínimo com Nginx

Adapte domínio e caminhos dos certificados:

```nginx
server {
    listen 80;
    server_name api.exemplo.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name api.exemplo.com;

    ssl_certificate /caminho/certificado.pem;
    ssl_certificate_key /caminho/chave-privada.pem;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-For $remote_addr;
        proxy_set_header Forwarded "";
    }
}
```

Esse exemplo considera que o Nginx recebe diretamente a conexão pública. Se existir outro balanceador na frente dele,
configure explicitamente quais proxies são confiáveis antes de preservar endereços encaminhados.

O cliente da Fase 8 aceita apenas esquema, host e porta. Publique a API na raiz do domínio. Endereços com caminho, como
`https://exemplo.com/missaomarte`, não são aceitos pelo cliente atual.

## 7. Persistência do H2 e backup

O banco usa esta URL:

```text
jdbc:h2:file:${MISSAOMARTE_DADOS}/missaomarte
```

O arquivo principal é:

```text
${MISSAOMARTE_DADOS}/missaomarte.mv.db
```

Arquivos de bloqueio ou temporários podem existir enquanto o processo está aberto. Em hospedagens com sistema de arquivos
efêmero, monte um volume persistente no diretório indicado por `MISSAOMARTE_DADOS`. Sem isso, contas, partidas e ranking
podem desaparecer em um novo deploy.

O projeto usa `spring.jpa.hibernate.ddl-auto=update`, sem migrações versionadas. Faça um backup antes de publicar uma nova
versão do servidor.

### 7.1 Backup recomendado

1. Pare o processo Java de forma graciosa.
2. Confirme que ele terminou.
3. Copie ou compacte todo o diretório de dados.
4. Reinicie o servidor.
5. Guarde o backup fora da máquina ou volume principal.

Exemplo com o processo parado:

```bash
mkdir -p "$HOME/backups/missaomarte"
tar -C "$MISSAOMARTE_DADOS" \
    -czf "$HOME/backups/missaomarte/missaomarte-$(date +%Y%m%d-%H%M%S).tar.gz" .
```

Não copie `missaomarte.mv.db` enquanto a aplicação estiver gravando. O H2 permite backup online com o comando SQL
`BACKUP TO`, mas a aplicação atual não expõe uma operação administrativa para executá-lo. Para este projeto, parar o
servidor e copiar o diretório é o procedimento mais simples e seguro.

### 7.2 Testar uma restauração

Não restaure por cima de um banco aberto. Use um diretório novo:

```bash
mkdir -p /caminho/restauracao-missaomarte
tar -xzf /caminho/backup-missaomarte.tar.gz -C /caminho/restauracao-missaomarte
export MISSAOMARTE_DADOS=/caminho/restauracao-missaomarte
java -jar build/libs/app.jar
```

Confira `/api/saude`, faça login com uma conta de teste e consulte o ranking. Só depois considere o backup validado.

## 8. Desativar o Swagger em produção

Defina as duas variáveis antes de iniciar o servidor:

```bash
export SPRINGDOC_SWAGGERUI_ENABLED=false
export SPRINGDOC_APIDOCS_ENABLED=false
```

Depois da inicialização, verifique:

```bash
curl -o /dev/null -s -w "%{http_code}\n" https://api.exemplo.com/swagger-ui.html
curl -o /dev/null -s -w "%{http_code}\n" https://api.exemplo.com/v3/api-docs
```

O resultado esperado é `404` para os dois endereços. O endpoint público `/api/saude` permanece disponível para o proxy e
para a plataforma de hospedagem.

## 9. Configurar o cliente desktop

Há duas formas de escolher o servidor.

### 9.1 Pela interface

1. Abra `Menu > Opções > Servidor`.
2. Informe somente o host, por exemplo `api.exemplo.com`.
3. Informe a porta `443`.
4. Ative HTTPS.
5. Use `Testar conexão`.
6. Use `Salvar`.

A configuração salva pelo jogador prevalece sobre o endereço padrão da inicialização. Se o endereço padrão mudar e o jogo
continuar usando o antigo, abra essa tela e use `Restaurar padrão` ou salve o novo endereço. Trocar de servidor encerra o
login atual porque o token pertence ao servidor anterior.

### 9.2 Pela inicialização do desktop

Na pasta `cliente`:

```bash
./gradlew :lwjgl3:run -Dmissaomarte.servidor=https://api.exemplo.com
```

A propriedade define somente o padrão. Uma configuração salva anteriormente continua tendo precedência.

## 10. Configurar o cliente Android

O endereço padrão do Android é definido durante o build:

```bash
cd fases/fase-08-campanha-fundadores/cliente
./gradlew :android:assembleDebug -PservidorPadrao=https://api.exemplo.com
```

Use a mesma propriedade no processo de geração e assinatura do release:

```bash
./gradlew :android:assembleRelease -PservidorPadrao=https://api.exemplo.com
```

O APK release precisa ser assinado pelo procedimento de distribuição escolhido pela equipe. Esta configuração apenas
embute o endereço padrão.

No aparelho, o jogador também pode usar `Menu > Opções > Servidor`, informar host, porta `443`, ativar HTTPS, testar e
salvar. A configuração salva no aparelho prevalece sobre o valor embutido no APK.

O build release bloqueia HTTP. Use um certificado reconhecido pelo Android e HTTPS para qualquer servidor publicado.

## 11. Publicação com Docker

O `Dockerfile` existente faz o build em uma imagem com JDK 17 e executa o resultado em uma imagem com JRE 17, usando um
usuário sem privilégios. Dentro do contêiner:

- `SERVER_ADDRESS=0.0.0.0` permite que a porta seja alcançada pela rede do contêiner.
- `MISSAOMARTE_DADOS=/dados` direciona o H2 para o volume.
- `/dados` é declarado como volume.
- O processo executa `java -jar app.jar`.

Na pasta `servidor`, gere a imagem:

```bash
docker build -t missao-marte-servidor .
```

Crie o volume persistente:

```bash
docker volume create missaomarte-dados
```

Para testar localmente atrás de um proxy na mesma máquina, publique a porta somente em `127.0.0.1`:

```bash
export MISSAOMARTE_JWT_SEGREDO="$(openssl rand -base64 48)"
docker run --rm --name missao-marte-servidor \
    --publish 127.0.0.1:8080:8080 \
    --volume missaomarte-dados:/dados \
    --env MISSAOMARTE_JWT_SEGREDO \
    --env SPRINGDOC_SWAGGERUI_ENABLED=false \
    --env SPRINGDOC_APIDOCS_ENABLED=false \
    missao-marte-servidor
```

Para um teste direto na rede local, troque a publicação por `--publish 8080:8080` e limite a porta no firewall à rede
confiável.

Em um provedor de nuvem:

1. Configure a pasta `servidor` como contexto do build.
2. Cadastre `MISSAOMARTE_JWT_SEGREDO` no gerenciador de segredos.
3. Monte um volume persistente em `/dados`.
4. Desative Swagger com as duas variáveis do springdoc.
5. Use `GET /api/saude` como verificação de saúde.
6. Exponha o contêiner somente pelo proxy HTTPS do provedor.
7. Confirme que reiniciar ou substituir o contêiner não apaga o volume.

Planos gratuitos frequentemente usam sistemas de arquivos efêmeros e podem suspender o serviço. A primeira chamada após
uma suspensão pode ultrapassar o tempo limite de 5 segundos do cliente. Use `Testar conexão` novamente depois que a
instância acordar.

## 12. Checklist antes de publicar

- [ ] `java -version` informa Java 17.
- [ ] `./gradlew clean test bootJar` termina sem falhas.
- [ ] `build/libs/app.jar` existe e inicia com `java -jar`.
- [ ] `MISSAOMARTE_JWT_SEGREDO` foi gerado fora do repositório e permanecerá estável entre reinicializações.
- [ ] O segredo não aparece em código, documentação, logs, capturas ou histórico versionado.
- [ ] `MISSAOMARTE_DADOS` aponta para um caminho ou volume persistente.
- [ ] O backup foi criado e uma restauração foi testada.
- [ ] Swagger UI e OpenAPI estão desativados.
- [ ] `/api/saude` responde pela URL pública.
- [ ] A internet alcança somente o proxy HTTPS, não a porta interna do Java.
- [ ] O proxy substitui os cabeçalhos encaminhados e não confia nos enviados pelo cliente público.
- [ ] O certificado TLS é válido e reconhecido pelo Android.
- [ ] O servidor está na raiz do domínio, sem prefixo adicional de caminho.
- [ ] Cadastro, login, registro de partida rápida e ranking foram testados.
- [ ] O cliente desktop aponta para o domínio publicado.
- [ ] O APK foi gerado com o domínio publicado ou configurado pela tela `Servidor`.
- [ ] A configuração salva antiga foi removida ou atualizada nos aparelhos usados na demonstração.
- [ ] As limitações acadêmicas, inclusive ausência de limitação de login e validação da pontuação, foram aceitas.

## 13. Referências técnicas

- [Configuração externa do Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Servidor Spring Boot atrás de proxy](https://docs.spring.io/spring-boot/how-to/webserver.html)
- [Propriedades do springdoc](https://springdoc.org/properties.html)
- [Arquivos e backup do H2](https://h2database.com/html/features.html)

