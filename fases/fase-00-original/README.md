> Base: Fase 00 do repositório do Prof. Marcelo Bezerra (T200-ProjArqSist), uso autorizado em sala. Alterações da equipe listadas abaixo.

# Fase 00: original

Origem: `missaoMarteUnifor/projetoExtensaoFinal/exercicio10/fases/fase-0-oopuro`, revisão `4b43e59eacfa57798d55b68c5d018015c804193e` da base. Os fontes e o README dentro de `src` foram preservados. A única adição da equipe é este README com execução no macOS.

Na pasta desta fase:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
mkdir -p out
find src -name '*.java' -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d out
java -cp out Main
```

Controles: w/a/s/d, c para embarcar, q para abortar. Ranking Top 5 em `ranking.json` local e ignorado pelo Git. Não existe suíte automatizada na base original.

Limitação preservada: tamanho de mapa 1 com médio/difícil não comporta os objetos e pode prender o sorteio. Para executar esta fase use tamanho 5. A validação desse caso existe apenas na Fase 1 da equipe.
