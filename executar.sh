#!/usr/bin/env bash
# MoveCity - compila e executa no Linux e no macOS.
# Uso:  ./executar.sh        (na pasta onde está o README.md)
set -e

cd "$(dirname "$0")"

# 1) Confere se o Java (JDK) está instalado
if ! command -v javac >/dev/null 2>&1; then
  echo "ERRO: JDK não encontrado. Instale o JDK 17 ou superior e tente novamente."
  exit 1
fi

# 2) Procura o driver do MySQL (arquivo .jar) na pasta lib/
DRIVER=$(ls lib/*.jar 2>/dev/null | head -n 1 || true)
if [ -z "$DRIVER" ]; then
  echo "ERRO: coloque o driver MySQL Connector/J (arquivo .jar) dentro da pasta lib/."
  echo "Veja o README.md, seção \"Driver do MySQL\"."
  exit 1
fi

# 3) Compila todo o código-fonte para a pasta out/
rm -rf out
mkdir -p out
javac -encoding UTF-8 -cp "$DRIVER" -d out $(find src -name "*.java")
echo "Compilação concluída."

# 4) Executa (no Linux/macOS o separador do classpath é ":")
java -Dfile.encoding=UTF-8 -cp "out:$DRIVER" Main
