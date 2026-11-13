# Publicar o servidor do Missão Marte (teste acadêmico)

Este roteiro serve para o celular conseguir falar com o servidor de ranking. **É um exemplo acadêmico**: não há alta
disponibilidade, backup, limite de tentativas de login nem monitoramento. Use dados de teste (nomes e senhas inventados).

Há dois caminhos. Escolha o mais simples para o seu caso.


| Caminho | Quando usar | Custo | HTTPS |
|---|---|---|---|
| A. Rede Wi-Fi local (LAN) | Celular e PC na mesma rede | Nenhum | Não (HTTP) |
| B. Render com Docker | Acessar de qualquer rede | Depende do plano; o gratuito serve para demonstração, com limitações | Sim, pelo Render |

## Variáveis de ambiente do servidor


| Variável                 | Padrão            | Para que serve                                                                                                                |
| --------------------------- | -------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `SERVER_ADDRESS`          | `127.0.0.1`        | Endereço em que o servidor escuta. `0.0.0.0` aceita conexões de outros aparelhos.                                           |
| `PORT`                    | `8080`             | Porta. Provedores de nuvem costumam informar a porta por esta variável.                                                      |
| `MISSAOMARTE_JWT_SEGREDO` | vazio (aleatório) | Segredo que assina os tokens (mínimo 32 caracteres). Vazio: gera um a cada início e os tokens deixam de valer ao reiniciar. |
| `MISSAOMARTE_DADOS`       | `./dados`          | Pasta do banco H2 (arquivo).                                                                                                  |

O servidor tem o endpoint público `GET /api/saude`, que responde `{"status":"ok"}`. O jogo usa esse endpoint no botão
**Testar conexão** da tela Servidor, e os provedores de nuvem podem usá-lo como verificação de saúde.

## Caminho A: Wi-Fi local

1. Gere o JAR e inicie o servidor aceitando outros aparelhos (PowerShell, na pasta `servidor`). São **comandos separados: execute um por vez** (um Enter em cada linha). Se colar tudo de uma vez e o terminal juntar as linhas, o `bootJar` recebe os demais textos como argumentos e falha.

   Gerar o JAR:

   ```powershell
   .\gradlew.bat bootJar
   ```

   Definir as variáveis (valem só neste terminal) e iniciar:

   ```powershell
   $env:SERVER_ADDRESS = "0.0.0.0"
   $env:MISSAOMARTE_JWT_SEGREDO = "troque-por-um-texto-com-32-ou-mais-caracteres"
   & "$env:JAVA_HOME\bin\java.exe" -jar build\libs\app.jar
   ```

   Ou, em uma única linha (comandos separados por `;`):

   ```powershell
   $env:SERVER_ADDRESS = "0.0.0.0"; $env:MISSAOMARTE_JWT_SEGREDO = "troque-por-um-texto-com-32-ou-mais-caracteres"; & "$env:JAVA_HOME\bin\java.exe" -jar build\libs\app.jar
   ```

   Sem `SERVER_ADDRESS=0.0.0.0` o servidor aceita só conexões do próprio PC e o celular não consegue chegar nele.
2. Descubra o IP do PC na rede: `ipconfig` e procure o **Endereço IPv4** do adaptador Wi-Fi (por exemplo `192.168.0.10`).
3. Libere a porta no Firewall do Windows. Na primeira execução o Windows pergunta se o Java pode receber conexões:
   permita apenas em **rede privada**. Para criar a regra manualmente (PowerShell como administrador):

   ```powershell
   New-NetFirewallRule -DisplayName "Missao Marte 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Private
   ```

   Remova a regra ao terminar: `Remove-NetFirewallRule -DisplayName "Missao Marte 8080"`.
4. No celular, abra o jogo, vá em **Servidor** (opção 6 do menu), informe o IP e a porta (`192.168.0.10` e `8080`), deixe
   HTTPS desligado, toque em **Testar conexão** e depois em **Salvar**.

Alternativa sem Wi-Fi, com o celular ligado por cabo USB e a depuração USB ativa: `adb reverse tcp:8080 tcp:8080`.
O celular passa a enxergar o servidor do PC como `localhost:8080` (o endereço padrão do app).

> A conexão é HTTP, sem criptografia: senha e token trafegam em texto claro. Use só em rede de confiança (a sua, em casa
> ou no laboratório), nunca em Wi-Fi público.

## Docker: conceitos usados neste projeto

Docker empacota uma aplicação e seus arquivos de execução em uma **imagem**. A imagem é um artefato versionado que pode ser usado para criar um ou mais **contêineres**. Um contêiner é um processo isolado que executa a imagem usando o kernel do sistema hospedeiro; não é uma máquina virtual completa. A imagem ajuda a tornar o ambiente de execução mais reproduzível, mas não garante por si só disponibilidade, segurança ou persistência dos dados.

Neste projeto:

- O `Dockerfile` é a receita que descreve como construir a imagem.
- `eclipse-temurin:17-jdk` é a imagem-base do estágio de **build**, que tem ferramentas para compilar o servidor.
- O primeiro `FROM ... AS build` e o segundo `FROM ...jre` formam um **build multi-stage**. O estágio final recebe apenas o JAR e uma JVM de execução; não inclui o JDK, Gradle ou fontes. Isso reduz o conteúdo necessário em runtime.
- `COPY` adiciona arquivos à imagem; `RUN` executa comandos durante a construção; `ENV` define padrões de configuração; `ENTRYPOINT` informa o processo iniciado pelo contêiner.
- `USER app` executa o processo sem privilégios administrativos. A pasta `/dados` é criada e atribuída a esse usuário.
- `EXPOSE 8080` documenta a porta padrão do contêiner, mas não publica a porta nem força o Spring a escutá-la. O servidor usa a variável `PORT` e o Render define a porta pública de destino.
- Um **volume** é armazenamento separado do ciclo de vida do contêiner. O H2 grava o banco em `/dados`; sem um volume persistente montado nesse caminho, os arquivos do banco podem desaparecer quando o contêiner reiniciar ou for substituído.

Construir uma imagem é diferente de executá-la: `docker build` cria a imagem; `docker run` cria e inicia um contêiner a partir dela. No Render, o provedor constrói a imagem do repositório, armazena-a em seu registry e inicia o serviço usando essa imagem.

O build local opcional, executado na pasta `servidor` com o Docker Desktop ativo, pode ser feito assim:

```powershell
docker build -t missao-marte-servidor .
docker run --rm -p 8080:8080 -v missaomarte-dados:/dados `
  -e MISSAOMARTE_JWT_SEGREDO="use-um-segredo-de-teste-com-pelo-menos-32-bytes" `
  missao-marte-servidor
```

`-p 8080:8080` encaminha a porta 8080 do computador para a porta 8080 do contêiner. `-v missaomarte-dados:/dados` monta um volume Docker nomeado para que o arquivo H2 permaneça mesmo depois de remover o contêiner. Esse segredo é apenas um exemplo para teste local: crie um valor aleatório e nunca reutilize um segredo real em comando gravado, captura de tela ou repositório.

## Caminho B: publicar no Render com Docker

O Render pode construir o serviço web diretamente do `Dockerfile` conectado ao Git. O endereço público recebe HTTPS no balanceador do Render; o tráfego entre esse balanceador e o contêiner é encaminhado à porta HTTP informada por `PORT`.

### 1. Preparar o repositório

1. Envie para um repositório Git acessível pelo Render os arquivos do servidor, incluindo `Dockerfile` e `.dockerignore`.
2. Este repositório contém mais de um projeto. Na criação do serviço, configure a raiz para a pasta do servidor, relativa à raiz do repositório:

   ```text
   missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/fase-06-android/servidor
   ```

   Se o projeto estiver em outro repositório ou caminho, ajuste esse valor. O Render só disponibiliza ao build os arquivos dentro da raiz configurada; neste caso isso basta, pois o Dockerfile compila o projeto Spring Boot autocontido em `servidor/`.

### 2. Criar o serviço web

1. Entre no [Render Dashboard](https://dashboard.render.com/) e escolha **New > Web Service**.
2. Conecte o provedor Git e selecione o repositório que contém o projeto.
3. Configure:

   | Campo no Render | Valor |
   |---|---|
   | Name | Um nome disponível, por exemplo `missao-marte-servidor` |
   | Root Directory | `missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/fase-06-android/servidor` |
   | Language / Runtime | `Docker` |
   | Dockerfile Path | `Dockerfile` (relativo à raiz configurada) |
   | Docker Context Directory | `.` (a própria raiz configurada) |
   | Health Check Path | `/api/saude` |
   | Instance Type | `Free` para demonstração, ou um plano pago se precisar de disco persistente |

   Não configure um comando de build ou start para substituir os padrões do Dockerfile: o Render constrói a imagem e usa o `ENTRYPOINT` já definido.

### 3. Configurar a variável secreta

Antes de criar ou iniciar o serviço, cadastre `MISSAOMARTE_JWT_SEGREDO` em **Environment**. Use um segredo aleatório, exclusivo deste ambiente e com pelo menos 32 bytes; um gerenciador de senhas pode gerar esse valor. Não use a senha de uma conta, o texto de exemplo acima nem coloque o valor no Git.

O `Dockerfile` já define `SERVER_ADDRESS=0.0.0.0` e `MISSAOMARTE_DADOS=/dados`. A aplicação lê a variável `PORT` fornecida pelo Render; não fixe `8080` nas variáveis do serviço. `EXPOSE 8080` não impede a aplicação de escutar na porta dinâmica recebida pelo Spring.

### 4. Iniciar e verificar o deploy

1. Clique em **Create Web Service**. Na primeira implantação, o Render baixa as imagens-base, compila o JAR no estágio JDK e constrói a imagem final com a JVM JRE.
2. Acompanhe a página **Events/Deploys** e os logs. A implantação deve terminar como **Live**; erros de compilação ou inicialização aparecem nos logs.
3. Copie o domínio `https://<nome-do-servico>.onrender.com` exibido no painel e abra:

   ```text
   https://<nome-do-servico>.onrender.com/api/saude
   ```

   A resposta esperada é `{"status":"ok"}`. Também é possível testar `/swagger-ui.html`; para um serviço real, a documentação interativa pode ser desligada com `SPRINGDOC_SWAGGERUI_ENABLED=false` e `SPRINGDOC_APIDOCS_ENABLED=false`.
4. No jogo, abra **Servidor** e configure o host `<nome-do-servico>.onrender.com`, porta `443` e HTTPS ligado. Toque em **Testar conexão** e depois em **Salvar**. Não inclua `https://` no campo de host: o esquema e a porta são configurados separadamente.
5. Se o servidor responder no navegador mas não no jogo, confira o domínio, a porta 443, HTTPS ligado e os logs do serviço. O Render encerra TLS no balanceador; o aplicativo usa os cabeçalhos encaminhados pelo proxy para reconhecer a requisição HTTPS original.

Para embutir o endereço como padrão no APK de debug, execute no projeto cliente:

```powershell
.\gradlew.bat :android:assembleDebug "-PservidorPadrao=https://<nome-do-servico>.onrender.com"
```

Substitua o exemplo pelo domínio real, sem `<` e `>`. Não inclua barra final. O jogador ainda pode mudar o servidor na tela de configuração.

### 5. Persistência e plano gratuito

O plano Free é apropriado para demonstração e testes, não para dados importantes. Segundo a documentação atual do Render, serviços web gratuitos podem ser suspensos após 15 minutos sem tráfego e voltam a iniciar quando recebem uma nova chamada; a partida a frio pode levar cerca de um minuto. O filesystem padrão é **efêmero**: alterações locais são perdidas em reinicialização, suspensão ou novo deploy. Como o banco deste exemplo é H2 em arquivo dentro de `/dados`, contas e partidas podem desaparecer.

Discos persistentes do Render exigem um serviço elegível em plano pago. Se optar por esse caminho:

1. Na configuração do serviço, adicione um **Persistent Disk** com mount path `/dados`.
2. Mantenha `MISSAOMARTE_DADOS=/dados` (valor já definido pelo Dockerfile).
3. Salve a alteração e aguarde o novo deploy; verifique os logs e teste com uma conta de demonstração.

Um disco persistente protege os arquivos contra o ciclo normal de deploy/reinício, mas não substitui estratégia de backup, banco gerenciado ou validação de recuperação. Este servidor H2 foi desenvolvido para uma instância e uso acadêmico, não para escalar horizontalmente ou atender dados reais. Para produção, use um banco apropriado, migrações versionadas, backups, monitoramento e controles de segurança adicionais.

### Limites a conhecer

- **Os dados podem ser perdidos.** Sem disco persistente, o H2 em arquivo é efêmero no Render. O plano Free não deve ser usado para armazenar dados importantes.
- **Partida a frio.** O plano Free pode suspender o serviço após inatividade. O primeiro acesso pode demorar mais que o limite de cinco segundos do jogo; tente novamente após o serviço voltar.
- **Segredo dos tokens.** Sem `MISSAOMARTE_JWT_SEGREDO`, os tokens deixam de valer a cada reinício e o jogador precisa
  entrar de novo.
- **Swagger.** A documentação interativa (`/swagger-ui.html`) fica ligada para facilitar o teste. Para desligar, defina
  `SPRINGDOC_SWAGGERUI_ENABLED=false` e `SPRINGDOC_APIDOCS_ENABLED=false` (os hífens do nome da propriedade somem na variável de ambiente).
- **Sem proteção contra tentativas em massa de login.** Não deixe um serviço assim publicado depois do teste.
  Apague o serviço quando terminar.

Consulte as [limitações atuais do plano Free do Render](https://render.com/docs/free), pois planos, cotas e regras comerciais podem mudar.

## Segurança em resumo

- HTTP puro só em rede local de confiança. Na internet, só HTTPS (o provedor cuida do certificado).
- O app Android de **debug** aceita HTTP (para LAN e emulador); o build **release** só aceita HTTPS.
- O jogo guarda no aparelho apenas endereço, porta e HTTPS. A senha e o token ficam só na memória e se perdem ao fechar o app.
- O jogo avisa na tela Servidor quando o endereço usa HTTP fora da rede local.

## Referências do Render e Docker

- Render, deploy de serviços Docker: <https://render.com/docs/docker>
- Render, criação e configuração de Web Services: <https://render.com/docs/web-services>
- Render, diretório raiz em monorepos: <https://render.com/docs/monorepo-support>
- Render, variáveis de ambiente e segredos: <https://render.com/docs/configure-environment-variables>
- Render, health checks: <https://render.com/docs/health-checks>
- Render, discos persistentes: <https://render.com/docs/disks>
- Render, limitações do plano Free: <https://render.com/docs/free>
- Docker, build multi-stage: <https://docs.docker.com/build/building/multi-stage/>
