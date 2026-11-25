# Etapa 2: importação e validação das Fases 2 a 7

Data: 12/11/2026. Escopo aprovado: importar as Fases 2 a 7, registrar a origem, adaptar comandos para macOS e validar cada projeto sem alterar suas versões ou regras. A Etapa 3 e a Fase 8 não foram iniciadas.

## Resultado

A importação e as adaptações de documentação estão preparadas no workspace. Os nove builds foram tentados e falharam no download da distribuição do Gradle, antes da compilação e dos testes, com `java.net.UnknownHostException: services.gradle.org`. Isso documenta uma limitação do ambiente; não comprova que os projetos compilam nem que seus testes passam.

Os commits estão pendentes porque a sessão não pode escrever em `.git`. Não houve push.

## Origem e preservação

- Base externa: `../base-professor/missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/`.
- Revisão: `4b43e59eacfa57798d55b68c5d018015c804193e`, do [repositório do professor](https://github.com/marcelobezerra-dotcom/T200-ProjArqSist/tree/4b43e59eacfa57798d55b68c5d018015c804193e/missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases).
- Uso autorizado em sala, conforme `CLAUDE.md` e `AGENTS.md`. A base foi consultada somente para leitura; nenhum build foi executado nela.
- As cópias excluem `.git/`, `.gradle/`, `build/`, `out/`, arquivos `.class`, configurações locais da IDE, `ranking*.json`, `local.properties` e bancos locais H2.
- Os 771 arquivos foram comparados por SHA-256 à base antes das adaptações. Depois das adaptações, os 537 arquivos Java continuam idênticos à base, assim como assets, testes, configurações de build, dependências e conteúdo dos Wrappers.
- Os 116 arquivos existentes nas Fases 0 e 1, incluindo os artefatos locais já presentes, foram comparados ao inventário anterior à importação e permanecem idênticos. Essas fases não foram recompiladas nesta etapa.

| Pasta em `fases/` | Arquivos importados | Conteúdo alterado pela equipe | Permissão alterada |
|---|---:|---|---|
| `fase-02-gradle` | 43 | `README.md`, link em `docs/fase-02-tutorial.md` | `gradlew` executável |
| `fase-03-libgdx` | 55 | `README.md`, link em `docs/fase-03-tutorial.md` | `gradlew` executável |
| `fase-04-interface-grafica` | 101 | `README.md`, link em `docs/fase-04-tutorial.md` | `gradlew` executável |
| `fase-05-servidor-ranking` | 162 | `README.md` | `cliente/gradlew`, `servidor/gradlew` executáveis |
| `fase-06-android` | 194 | `README.md` | `cliente/gradlew`, `servidor/gradlew` executáveis |
| `fase-07-evolucao-livre` | 216 | `README.md` | `cliente/gradlew`, `servidor/gradlew` executáveis |

Os READMEs receberam aviso de origem, distinção entre evidência histórica e validação da equipe, comandos `./gradlew` e caminhos relativos à raiz deste repositório. Os três links de tutoriais passaram a apontar para o arquivo da revisão fixada no GitHub. O restante da documentação herdada foi preservado, inclusive exemplos e relatos do ambiente do professor.

A Fase 1 da equipe mantém `c` e Top 5. As importações preservam `e`/`E` no console/modo clássico, Top 10 e, na Fase 7, modo dinâmico com embarque automático. Elas são outra trilha de código; não representam uma migração dos fontes da equipe para Gradle. A evolução livre importada é do professor; a melhoria própria da equipe será a Fase 8.

## Ambiente e comando utilizado

JDK selecionado: Temurin **17.0.21+9**, em `/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`. O JDK foi selecionado explicitamente, pois a configuração padrão do terminal não garante Java 17.

Preparação, a partir da raiz do repositório:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
export GRADLE_USER_HOME="$PWD/.gradle/usuario"
java -version
javac -version
```

Comando tentado em cada um dos nove diretórios da tabela seguinte:

```bash
./gradlew clean build --console=plain --no-daemon
```

Não havia distribuição do Gradle nem dependências em cache disponíveis para esta validação. O teste inicial de `./gradlew --version` na Fase 2 também foi interrompido no download. O Wrapper tentou as URLs de `services.gradle.org/distributions/` configuradas pela base; nenhuma versão foi trocada para contornar a falha.

## Resultado por projeto

Todos os diretórios abaixo são relativos à raiz. Cada linha representa uma tentativa independente do comando acima, usando o Wrapper daquele projeto.

| Diretório | Gradle configurado | Código de saída | Resultado |
|---|---|---:|---|
| `fases/fase-02-gradle` | 8.7 | 1 | Falha de DNS no download |
| `fases/fase-03-libgdx` | 8.7 | 1 | Falha de DNS no download |
| `fases/fase-04-interface-grafica` | 8.7 | 1 | Falha de DNS no download |
| `fases/fase-05-servidor-ranking/cliente` | 8.7 | 1 | Falha de DNS no download |
| `fases/fase-05-servidor-ranking/servidor` | 9.8.2 | 1 | Falha de DNS no download |
| `fases/fase-06-android/cliente` | 8.7 | 1 | Falha de DNS no download |
| `fases/fase-06-android/servidor` | 9.8.2 | 1 | Falha de DNS no download |
| `fases/fase-07-evolucao-livre/cliente` | 8.7 | 1 | Falha de DNS no download |
| `fases/fase-07-evolucao-livre/servidor` | 9.8.2 | 1 | Falha de DNS no download |

Erro comum registrado nos nove logs:

```text
Exception in thread "main" java.net.UnknownHostException: services.gradle.org
```

Os logs completos e inventários de hashes estão localmente em `.gradle/etapa-02/`, ignorados pelo Git. `builds.json` relaciona diretório, comando, código de saída, motivo e arquivo de log. Este documento preserva o resumo versionável da evidência, sem depender desses arquivos locais.

**Testes: não executados.** O Wrapper falhou antes de iniciar o Gradle. Não há resultados JUnit desta importação. Números como 39, 66, 97, 141 e 208 testes nos documentos importados são registros históricos do professor, não contagens confirmadas nesta sessão.

## Verificações estáticas e limitações de execução

- Os nove `gradlew` têm permissão de execução e conteúdo idêntico à base.
- Os cinco `lwjgl3/build.gradle` já configuram `-XstartOnFirstThread` no macOS. Não foi necessário alterar launchers nem argumentos. A abertura das janelas ainda não foi verificada.
- Nas Fases 6 e 7, `cliente/settings.gradle` condiciona o módulo Android a um diretório SDK informado por `sdk.dir`, `ANDROID_HOME` ou `ANDROID_SDK_ROOT`. Não há SDK configurado nesta sessão. Como o Gradle não iniciou, o caminho que omite o módulo foi inspecionado no arquivo, mas não confirmado por execução.
- Console, janelas desktop, modo toque, API local e APK não foram executados/gerados, pois dependem dos builds. Nenhuma requisição de cadastro ou envio de partida foi feita.
- Nenhum teste de execução substituiu os builds bloqueados. A identidade dos fontes comprova a fidelidade da cópia, não a compatibilidade ou funcionamento no ambiente da equipe.

## Retomada da validação

1. Em terminal com acesso à internet, selecionar o JDK 17 e repetir `./gradlew clean build --console=plain --no-daemon` em cada diretório da tabela. Confirmar primeiro o download do Gradle; eventuais falhas posteriores de resolução ou compilação precisam de novo diagnóstico. Não alterar versões sem aprovação.
2. Somente após os builds passarem, conferir os relatórios `build/test-results/test/` e `build/reports/tests/test/` de cada projeto ou módulo, registrando testes executados, falhas e ignorados. Atualizar este documento com a evidência real.
3. Executar a Fase 2 com `./gradlew run --console=plain`; nas Fases 3 e 4 e nos clientes das Fases 5 a 7, executar `./gradlew :lwjgl3:run --console=plain`. Verificar menu, partida, embarque, término e ranking. Nas Fases 6 e 7, verificar também o modo toque conforme seus READMEs.
4. Testar um servidor por vez com `./gradlew bootRun --console=plain` no respectivo diretório `servidor`, evitando conflito na porta 8080. Seguir os fluxos de cadastro, login, envio de partida, histórico e ranking dos READMEs. Nas Fases 6 e 7, verificar também `/api/saude`. Encerrar cada servidor ao terminar. Usar variáveis de ambiente para dados locais e segredo JWT, sem versionar seus valores.
5. Com SDK configurado, executar `./gradlew :android:assembleDebug` nos clientes das Fases 6 e 7 e conferir o APK em `android/build/outputs/apk/debug/`. Sem SDK, registrar explicitamente a validação limitada ao desktop/servidor; não instalar SDK nem mudar dependências nesta etapa sem novo acordo.

## Commits pendentes

O histórico local e a referência local `origin/main` apontam para `8842ac4`, após `36af8b6`, os dois commits da Etapa 1. A tentativa de criar a branch `etapa-02-importacao-base` falhou porque não foi possível criar o arquivo de lock em `.git/refs/heads/` (`Operation not permitted`). A sessão permaneceu em `main`, com as alterações no workspace. Nenhum commit novo foi criado.

Sequência prevista para registro em ambiente com escrita permitida no Git:

1. `chore(fase-02): importa base do professor (MM-03)`
2. `chore(fase-03): importa base do professor (MM-04, MM-05)`
3. `chore(fase-04): importa base do professor (MM-06)`
4. `chore(fase-05): importa base do professor (MM-07, MM-08, MM-09)`
5. `chore(fase-06): importa base do professor (MM-10, MM-11, MM-12)`
6. `chore(fase-07): importa base do professor (referência MM-13, MM-14)`
7. `chore(fases): adapta documentação e execução para macOS (MM-03 a MM-14)`
8. `docs(repo): registra importação e limites da validação da etapa 2 (MM-03 a MM-14)`

Para preservar a separação exigida entre base e adaptações, os seis primeiros commits devem conter as cópias originais; o sétimo deve conter os seis READMEs adaptados, os três links corrigidos e as nove mudanças de permissão. O oitavo reúne o README da raiz e este registro. Antes de registrar, guardar as adaptações atuais, montar as importações originais a partir da revisão indicada e reaplicar as adaptações. Não registrar as pastas atuais como se fossem cópias sem alterações. A associação de MM-13/MM-14 à importação é apenas referência; não significa que a melhoria própria da equipe foi implementada.

## Revisão final

Uma revisão independente, somente leitura, conferiu as importações, as adaptações, os comandos, os links locais e os registros de validação. Não apontou problemas críticos, importantes ou menores. Essa avaliação é estática e não substitui os builds ou testes.

Decisões mantidas após a revisão:

- A correção funcional das regras herdadas não foi auditada: o escopo aprovado exige preservar a base. Eventuais defeitos herdados continuam sujeitos à validação funcional posterior.
- A compatibilidade efetiva das dependências e a execução desktop/API/Android permanecem sem confirmação por causa do bloqueio de download. Não houve troca de versões para contornar o ambiente; ainda podem surgir falhas depois que os downloads funcionarem.

## Critérios de encerramento

| Critério | Situação |
|---|---|
| Fases 2 a 7 em pastas independentes, sem artefatos locais | Conferido |
| Origem e adaptações documentadas | Preparado nos READMEs e neste registro |
| Fontes, versões e Fases 0/1 preservados | Conferido por comparação |
| Build/testes de cada projeto ou falha com motivo | Nove tentativas, falha de DNS documentada |
| Desktop, API e APK funcionando neste ambiente | Não verificado |
| Um commit de importação por fase e adaptações separadas | Pendente por restrição de escrita em `.git` |
| Fase 8 | Não iniciada; depende de aprovação própria |
