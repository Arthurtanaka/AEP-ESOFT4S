# MoveCity – Aplicação Web para Mobilidade Urbana Sustentável

**AEP – 4º Semestre (2026.2) | Engenharia de Software / Análise e Desenvolvimento de Sistemas | UniCesumar**

Repositório público: https://github.com/Arthurtanaka/AEP-ESOFT4S

## Sumário

1. [Equipe](#equipe)
2. [Sobre o projeto](#sobre-o-projeto)
3. [Lista de requisitos do sistema](#lista-de-requisitos-do-sistema)
4. [Cronograma de execução](#cronograma-de-execução)
5. [Tecnologias e arquitetura](#tecnologias-e-arquitetura)
6. [Estrutura do repositório](#estrutura-do-repositório)
7. [Como executar](#como-executar)
8. [Mapa de avaliação (2ª entrega)](#mapa-de-avaliação-2ª-entrega)
9. [Requisitos → onde estão no código](#requisitos--onde-estão-no-código)
10. [Regras de negócio → onde estão no código](#regras-de-negócio--onde-estão-no-código)
11. [Pilares de POO no código](#pilares-de-poo-no-código)
12. [Banco de dados: classes e tabelas](#banco-de-dados-classes-e-tabelas)
13. [API do sistema](#api-do-sistema)
14. [Testes realizados](#testes-realizados)
15. [Decisões e limitações](#decisões-e-limitações)

## Equipe

| Integrante | RA | Principais responsabilidades |
|---|---|---|
| Miguel Souza Colis | 25014158-2 | Pacote `model` (classes de domínio) e interface web |
| Arthur Riki Farias Tanaka | 25224963-2 | Repositório, banco de dados (`database`, pacote `dao`) e pacote `util` |
| João Gustavo Zanoni Gonçalves | 25357434-2 | Pacote `service` (regras de negócio), CRUD de Rota e pacote `controller` |

## Sobre o projeto

O MoveCity é uma aplicação web de mobilidade urbana sustentável. O usuário informa uma origem e um destino, consulta as alternativas de deslocamento cadastradas (rotas formadas por trechos, cada trecho feito com um meio de transporte) e as compara por **tempo, distância e impacto ambiental**, com destaque para a alternativa mais sustentável.

**Problema (descoberta):** congestionamentos e transporte urbano pouco eficiente. O excesso de carros particulares, a falta de integração entre os meios de transporte, as informações dispersas e a baixa previsibilidade levam a engarrafamentos, poluição do ar, maior tempo de deslocamento e perda de qualidade de vida.

**ODS vinculados:**

- **ODS 11 – Cidades e Comunidades Sustentáveis (principal):** metas 11.2 (sistemas de transporte seguros, acessíveis e sustentáveis) e 11.6 (redução do impacto ambiental negativo das cidades, com atenção à qualidade do ar).
- **ODS 13 – Ação contra a Mudança Global do Clima (complementar).**

## Lista de requisitos do sistema

| Código | Descrição |
|---|---|
| RQ01 | O sistema deve permitir o cadastro de usuários, com nome completo, e-mail, senha e perfil (passageiro ou administrador) e, para passageiros, a preferência de deslocamento (por exemplo, mais rápido ou mais sustentável), não permitindo dois cadastros com o mesmo e-mail. |
| RQ02 | O sistema deve permitir que o usuário cadastrado acesse o sistema informando e-mail e senha, liberando as funções de acordo com o seu perfil. |
| RQ03 | O sistema deve permitir ao administrador cadastrar, consultar, atualizar e excluir rotas, informando origem, destino, descrição e os trechos que as compõem (ordem, distância em km e meio de transporte de cada trecho). |
| RQ04 | O sistema deve permitir ao administrador cadastrar, consultar, atualizar e excluir meios de transporte, com nome, tipo (carro, ônibus ou bicicleta), velocidade média em km/h e emissão de CO2 em gramas por km. |
| RQ05 | O sistema deve permitir ao usuário informar uma origem e um destino e apresentar as alternativas de rota cadastradas para esse par, com os meios de transporte de cada trecho. |
| RQ06 | O sistema deve permitir comparar as alternativas lado a lado, exibindo para cada uma o tempo estimado em minutos, a distância total em km e o impacto ambiental estimado em gramas de CO2. |
| RQ07 | O sistema deve destacar a alternativa mais sustentável, isto é, a de menor impacto ambiental total, quando houver mais de uma alternativa disponível. |
| RQ08 | O sistema deve permitir ao usuário autenticado salvar uma rota como favorita, listar suas rotas favoritas e removê-las. |
| RQ09 | O sistema deve manter, para cada usuário autenticado, o histórico das consultas de rotas realizadas, registrando a rota consultada e a data e hora da consulta. |
| RQ10 | O sistema deve permitir ao administrador registrar alertas vinculados a uma rota, com mensagem e status (ativo ou inativo), e exibir os alertas ativos ao usuário que consultar essa rota. |

A **entidade principal** do sistema (alvo do CRUD completo) é a **Rota**.

## Cronograma de execução

| Data | Atividade | Responsável |
|---|---|---|
| **Fase 1 – Planejamento, escopo e estruturação (1ª entrega)** | | |
| 27/08/2026 | Levantamento de requisitos e definição do escopo | Todos os integrantes |
| 31/08/2026 | Modelagem do Diagrama de Classes e do DER | Miguel Souza Colis |
| 03/09/2026 | Criação e estruturação do repositório no GitHub | Arthur Riki Farias Tanaka |
| 06/09/2026 | Redação da Justificativa Técnica e Concepção do projeto | João Gustavo Zanoni Gonçalves |
| 09/09/2026 | Revisão geral do documento e formatação do PDF | Todos os integrantes |
| 11/09/2026 | Entrega final da 1ª etapa (PDF + link do GitHub) | Todos os integrantes |
| 05/11/2026 | Revisão e correção da 1ª entrega conforme o retorno dos professores (documento, README e repositório) | Todos os integrantes |
| **Fase 2 – Implementação, rigor e integração (2ª entrega)** | | |
| 09/11/2026 | Publicação do repositório com estrutura de pastas, README e schema.sql atualizados | Arthur Riki Farias Tanaka |
| 12/11/2026 | Implementação do pacote model (Usuario, Passageiro, Administrador, Rota, Trecho, Transporte, Carro, Onibus, Bicicleta, Favorito, Historico e Alerta) | Miguel Souza Colis |
| 16/11/2026 | Implementação da camada DAO e da conexão JDBC com o MySQL | Arthur Riki Farias Tanaka |
| 19/11/2026 | Implementação da camada Service (regras RN01 a RN09) e do CRUD completo de Rota | João Gustavo Zanoni Gonçalves |
| 23/11/2026 | Implementação do Controller e da interface web | Miguel Souza Colis e João Gustavo Zanoni Gonçalves |
| 26/11/2026 | Testes de execução do sistema completo e instruções de execução no README | Todos os integrantes |
| 30/11/2026 | Entrega final da 2ª etapa (código-fonte e repositório) | Todos os integrantes |

## Tecnologias e arquitetura

- **Linguagem:** Java 17 ou superior (orientação a objetos: herança, polimorfismo, composição, classe abstrata e interface).
- **Banco de dados:** MySQL 8.0 ou superior, acessado por JDBC (driver MySQL Connector/J).
- **Arquitetura em camadas:** `Model` → `DAO` → `Service` → `Controller`, mais a interface web.
- **Interface:** HTML5, CSS3 e JavaScript puro, que conversam com o back-end por HTTP, trocando dados em JSON.
- **Servidor web:** o `HttpServer` que já vem no Java (`com.sun.net.httpserver`), sem frameworks. A única dependência externa é o driver do MySQL.

```
Navegador (HTML + CSS + JS)  --HTTP/JSON-->  Controller  -->  Service  -->  DAO  --JDBC/SQL-->  MySQL
                                                      \_______  Model (entidades de domínio)  _______/
```

## Estrutura do repositório

```
AEP-ESOFT4S/
├── src/
│   ├── Main.java              Ponto de partida (monta DAOs, Services e Controllers)
│   ├── model/                 Entidades de domínio (POO)
│   ├── dao/                   Acesso ao banco com JDBC
│   ├── service/               Regras de negócio
│   ├── controller/            API HTTP (um controller por recurso) e servidor web
│   ├── util/                  Configuração, hash de senha, JSON, formatação
│   └── web/                   Interface: index.html, estilo.css, app.js
├── docs/                      PDFs das entregas (1ª e 2ª), diagramas e roteiro de testes
│   └── testes/teste_api.py    Teste automatizado da API
├── database/
│   ├── schema.sql             Criação do banco e das tabelas
│   └── dados_exemplo.sql      Dados de demonstração (opcional)
├── lib/                       Coloque aqui o driver MySQL Connector/J (.jar)
├── executar.sh                Compila e executa (Linux e macOS)
├── executar.bat               Compila e executa (Windows)
├── config.example.properties  Modelo do arquivo de configuração local
├── README.md
└── .gitignore
```

## Como executar

### 1. Pré-requisitos (Windows, Linux ou macOS)

| Programa | Versão | Para que serve |
|---|---|---|
| JDK (Java Development Kit) | 17 ou superior | Compilar e executar o sistema |
| MySQL Server | 8.0 ou superior | Banco de dados |
| Navegador | qualquer atual | Usar a interface |
| Git (opcional) | qualquer | Clonar o repositório |

Para conferir o Java, abra um terminal e digite `javac -version`.

### 2. Baixar o projeto

```
git clone https://github.com/Arthurtanaka/AEP-ESOFT4S.git
cd AEP-ESOFT4S
```

(Ou use o botão **Code > Download ZIP** no GitHub e extraia a pasta.)

### 3. Criar o banco de dados

Com o MySQL em execução, rode na pasta do projeto (o terminal pedirá a senha do MySQL):

```
mysql -u root -p -e "source database/schema.sql"
mysql -u root -p -e "source database/dados_exemplo.sql"
```

O segundo comando é opcional: carrega 6 rotas de exemplo e 2 usuários de demonstração. Os dois scripts podem ser executados mais de uma vez sem duplicar dados. Também é possível abrir os arquivos no MySQL Workbench e executá-los.

### 4. Driver do MySQL

O Java precisa do driver **MySQL Connector/J** para conversar com o banco. Baixe o arquivo `.jar` e coloque-o na pasta `lib/`:

- Site oficial (opção "Platform Independent", extraia o `.jar` do arquivo baixado): https://dev.mysql.com/downloads/connector/j/
- Ou direto do Maven Central: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/ (escolha uma versão 8.x e baixe o arquivo `mysql-connector-j-<versão>.jar`)

### 5. Configuração (usuário e senha do MySQL)

Copie `config.example.properties` para `config.local.properties` (mesma pasta) e informe a senha do seu MySQL em `db.senha`. Esse arquivo **não vai para o GitHub** (está no `.gitignore`), então a senha nunca fica pública. Se o MySQL estiver com usuário `root` e **sem senha**, não é preciso criar o arquivo.

Também é possível configurar por variáveis de ambiente: `MOVECITY_DB_URL`, `MOVECITY_DB_USUARIO`, `MOVECITY_DB_SENHA`, `MOVECITY_CODIGO_ADMIN` e `MOVECITY_PORTA`.

### 6. Executar

| Sistema | Como executar | Observação |
|---|---|---|
| **Windows** | Dê duplo clique em `executar.bat` ou rode `executar.bat` no Prompt de Comando | Usa `;` como separador do classpath e ajusta o terminal para UTF-8 (`chcp 65001`) |
| **Linux** | `chmod +x executar.sh` (só na primeira vez) e depois `./executar.sh` | Usa `:` como separador do classpath |
| **macOS** | `chmod +x executar.sh` (só na primeira vez) e depois `./executar.sh` | Igual ao Linux |

Os scripts compilam o código para a pasta `out/` e iniciam o sistema. Quando aparecer `[OK] Servidor iniciado`, abra no navegador: **http://localhost:8080**. Para encerrar, pressione `Ctrl+C`.

**Executar manualmente, sem os scripts** (na pasta do projeto, trocando `DRIVER` pelo caminho do `.jar` em `lib/`):

```
# Linux / macOS
javac -encoding UTF-8 -cp lib/DRIVER.jar -d out $(find src -name "*.java")
java -cp out:lib/DRIVER.jar Main

# Windows (Prompt de Comando)
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -cp lib\DRIVER.jar -d out @fontes.txt
java -cp "out;lib\DRIVER.jar" Main
```

### 7. Usando o sistema

- **Usuários de demonstração** (criados pelo `dados_exemplo.sql`):

  | Perfil | E-mail | Senha |
  |---|---|---|
  | Administrador | admin@movecity.com | admin123 |
  | Passageiro | passageiro@movecity.com | senha123 |

- **Criar conta nova:** em "Criar conta". Para o perfil **Administrador** é preciso informar o *código de administrador*, que por padrão é `movecity2026` (configurável em `codigo.admin`). Isso impede que qualquer pessoa vire administradora sozinha.
- **Consulta de exemplo:** origem `Centro`, destino `Zona Norte` (3 alternativas, com destaque da mais sustentável).
- **Administração** (somente administrador): CRUD de Rotas (entidade principal), CRUD de Transportes e Alertas.

### Problemas comuns

| Mensagem | Causa provável |
|---|---|
| `[ERRO] Não foi possível conectar ao banco de dados` | MySQL desligado, senha errada no `config.local.properties` ou `schema.sql` ainda não executado |
| `ERRO: coloque o driver MySQL Connector/J` | Falta o arquivo `.jar` na pasta `lib/` |
| `ERRO: JDK não encontrado` | JDK não instalado, ou instalado apenas o JRE |
| `Não foi possível iniciar o servidor na porta 8080` | A porta está em uso: mude `servidor.porta` no `config.local.properties` |
| Acentos estranhos no terminal do Windows | Use o `executar.bat` (ele já ajusta o UTF-8) |

## Mapa de avaliação (2ª entrega)

Onde encontrar a evidência de cada critério da AEP:

| Critério (peso) | O que a rubrica pede | Onde está a evidência |
|---|---|---|
| **Aderência ao Planejamento** (0,2) | Código reflete os requisitos da 1ª entrega | Todos os requisitos RQ01 a RQ10 estão implementados (veja a tabela [Requisitos → código](#requisitos--onde-estão-no-código)). Eles cobrem os requisitos RF01 a RF08 do documento original da 1ª entrega. A arquitetura (Java, MySQL, camadas Model/DAO/Service/Controller, interface HTML/CSS/JS por HTTP/JSON) é a mesma do documento |
| **Pilares de POO** (0,3) | Classe abstrata ou interface; herança; polimorfismo com `@Override`; código tipado e em CamelCase | Seção [Pilares de POO](#pilares-de-poo-no-código): classes abstratas `Usuario`, `Transporte` e `BaseController`; interface `CrudDAO<T>`; herança em três hierarquias; `@Override` em todas as subclasses |
| **CRUD Funcional** (0,3) | Create, Read, Update e Delete na entidade principal | Entidade **Rota**: `dao/RotaDAO.java`, `service/RotaService.java`, `controller/RotaController.java` e tela *Administração > Rotas*. Cada operação é testada em `docs/testes/teste_api.py` |
| **Persistência em BD** (0,2) | Conexão ativa; script SQL em `/database` compatível com as classes | `dao/ConexaoBanco.java` (JDBC), `database/schema.sql` e tabela [classes ↔ tabelas](#banco-de-dados-classes-e-tabelas). Nenhuma lista em memória substitui o banco: os dados permanecem após reiniciar o sistema |
| **Ambiente de Sistemas Operacionais** | Instruções claras de execução e compatibilidade de ambientes | Seção [Como executar](#como-executar): pré-requisitos, passo a passo, scripts para Windows, Linux e macOS, solução de problemas |
| **Contribuição de todos** | Commits de todos os integrantes | Histórico de commits do repositório; divisão de responsabilidades na tabela da [Equipe](#equipe) |

Observações sobre os pilares de POO e as convenções: classes em `PascalCase`, métodos e variáveis em `camelCase` e constantes em `MAIÚSCULAS_COM_SUBLINHADO` (padrão da linguagem Java). Todo o código é tipado (tipos explícitos e coleções genéricas, como `List<Rota>`).

## Requisitos → onde estão no código

| Requisito | Service (regra) | DAO (banco) | Controller (API) | Tela |
|---|---|---|---|---|
| RQ01 Cadastro de usuários | `UsuarioService.cadastrar` | `UsuarioDAO.inserir` | `UsuarioController` (`POST /api/usuarios`) | Criar conta |
| RQ02 Login | `UsuarioService.login`, `SessaoService` | `UsuarioDAO.buscarPorEmail` | `SessaoController` (`POST /api/sessao`) | Entrar |
| RQ03 CRUD de rotas | `RotaService` (`cadastrar`, `listar`, `buscarPorId`, `atualizar`, `excluir`) | `RotaDAO` | `RotaController` (`/api/rotas`) | Administração > Rotas |
| RQ04 CRUD de transportes | `TransporteService` | `TransporteDAO` | `TransporteController` (`/api/transportes`) | Administração > Transportes |
| RQ05 Consulta origem/destino | `RotaService.consultarAlternativas` | `RotaDAO.buscarPorOrigemDestino` | `ConsultaController` (`GET /api/consulta`) | Consultar rotas |
| RQ06 Comparação | `AlternativaComparada`; `Rota.calcularTempo/Distancia/Impacto` | — | `Conversor.alternativa` | Consultar rotas (cartões) |
| RQ07 Mais sustentável | `RotaService.destacar` | — | `ConsultaController` | Etiqueta "Mais sustentável" |
| RQ08 Favoritos | `FavoritoService` | `FavoritoDAO` | `FavoritoController` (`/api/favoritos`) | Favoritos |
| RQ09 Histórico | `HistoricoService`; registro em `RotaService.consultarAlternativas` | `HistoricoDAO` | `HistoricoController` (`/api/historico`) | Histórico |
| RQ10 Alertas | `AlertaService` | `AlertaDAO` | `AlertaController` (`/api/alertas`) | Administração > Alertas; avisos na consulta |

## Regras de negócio → onde estão no código

| Regra | Onde é aplicada |
|---|---|
| RN01 E-mail único | `UsuarioService.cadastrar` e restrição `UNIQUE` em `usuario.email` (`schema.sql`) |
| RN02 Senha só como hash | `util/SegurancaUtil.gerarHash` (SHA-256) usado em `UsuarioService`; `Usuario.senhaConfere` |
| RN03 Só administrador altera rotas, transportes e alertas | `service/Permissoes.exigirAdministrador`, chamado em todos os métodos de escrita dos Services |
| RN04 Rota com pelo menos um trecho; excluir rota exclui trechos | `RotaService.construirRota`; `ON DELETE CASCADE` em `trecho.id_rota` |
| RN05 Tempo e impacto = soma dos trechos | `Rota.calcularTempo/calcularImpacto`, `Trecho` e as subclasses de `Transporte` |
| RN06 Mais sustentável = menor impacto; empate pelo menor tempo | `RotaService.destacar` |
| RN07 Histórico só para usuário autenticado | `RotaService.consultarAlternativas` (só grava se há usuário) |
| RN08 Sem favorito repetido | `FavoritoService.favoritar` e restrição `UNIQUE (id_usuario, id_rota)` |
| RN09 Alerta inativo não aparece | `AlertaDAO.listarAtivosPorRota` |

## Pilares de POO no código

| Pilar | Onde |
|---|---|
| **Classe abstrata** | `model/Usuario.java`, `model/Transporte.java`, `controller/BaseController.java` |
| **Interface** | `dao/CrudDAO.java` (contrato de CRUD, implementado por `RotaDAO` e `TransporteDAO`) |
| **Herança** | `Passageiro` e `Administrador` herdam de `Usuario`; `Carro`, `Onibus` e `Bicicleta` herdam de `Transporte`; os oito controllers da API herdam de `BaseController` |
| **Polimorfismo com `@Override`** | `autenticar()` e `getPerfil()` (cada perfil com sua regra); `calcularTempo()` e `calcularImpacto()` (cada transporte com seu cálculo, chamados por `Trecho` e `Rota` sem saber o tipo concreto); `tratar()` em cada controller; métodos de `CrudDAO` em `RotaDAO` e `TransporteDAO` |
| **Composição 1:N** | `Rota` é composta por `Trecho`s (`Rota.adicionarTrecho`); no banco, `trecho.id_rota` com `ON DELETE CASCADE` |
| **Encapsulamento** | Atributos `private` com getters e setters em todas as classes do `model` |
| **Exceções próprias** | `RegraNegocioException` (regras de negócio, com código HTTP) e `DaoException` (erros de banco) |
| **Membros `static`** | Constantes (`Onibus.ESPERA_MEDIA_MINUTOS`, `Alerta.STATUS_ATIVO`) e classes utilitárias (`Formatador`, `SegurancaUtil`, `Json`, `Entrada`) |

## Banco de dados: classes e tabelas

| Classe Java | Tabela | Observação |
|---|---|---|
| `Usuario` → `Passageiro`, `Administrador` | `usuario` | Herança com tabela única: a coluna `tipo_usuario` diz qual subclasse criar; `preferencia` e `nivel_acesso` aceitam `NULL` |
| `Transporte` → `Carro`, `Onibus`, `Bicicleta` | `transporte` | Herança com tabela única: coluna `tipo`; `fator_congestionamento` (só carro) e `capacidade_passageiros` (só ônibus) aceitam `NULL` |
| `Rota` | `rota` | Entidade principal |
| `Trecho` | `trecho` | Composição: `id_rota` com `ON DELETE CASCADE`; `id_transporte` aponta para `transporte` |
| `Favorito` | `favorito` | `UNIQUE (id_usuario, id_rota)` |
| `Historico` | `historico` | Consultas de usuários autenticados |
| `Alerta` | `alerta` | Ligado à rota e ao administrador que o registrou |

As gravações que envolvem mais de uma tabela (cadastrar ou atualizar uma rota com seus trechos) usam **transação** (`commit`/`rollback`) em `RotaDAO`.

## API do sistema

A interface web usa esta API. Respostas e pedidos em JSON; o login devolve um *token* enviado no cabeçalho `Authorization: Bearer <token>`.

| Método e caminho | Quem pode | O que faz |
|---|---|---|
| `POST /api/usuarios` | Qualquer pessoa | Cadastra usuário |
| `POST /api/sessao` · `GET /api/sessao` · `DELETE /api/sessao` | Qualquer pessoa / logado | Login · quem sou eu · logout |
| `GET /api/consulta?origem=...&destino=...` | Qualquer pessoa (grava histórico se logado) | Consulta, compara e destaca alternativas |
| `GET /api/rotas` · `GET /api/rotas/{id}` | Qualquer pessoa | Lista e busca rotas |
| `POST /api/rotas` · `PUT /api/rotas/{id}` · `DELETE /api/rotas/{id}` | Administrador | Cria, atualiza e exclui rotas |
| `GET /api/transportes` | Qualquer pessoa | Lista transportes |
| `POST /api/transportes` · `PUT /api/transportes/{id}` · `DELETE /api/transportes/{id}` | Administrador | CRUD de transportes |
| `GET/POST /api/favoritos` · `DELETE /api/favoritos/{idRota}` | Logado | Favoritos |
| `GET /api/historico` | Logado | Histórico de consultas |
| `GET/POST /api/alertas` · `PUT /api/alertas/{id}` | Administrador | Registra, lista e ativa/inativa alertas |

## Testes realizados

- **Teste automatizado da API:** `docs/testes/teste_api.py` (66 verificações) cobre RQ01 a RQ10, RN01 a RN09, permissões, validações, cálculos (conferidos à mão), CRUD completo de Rota e de Transporte e tratamento de erros. Resultado no ambiente de desenvolvimento: **66 aprovadas, 0 falhas**. Como rodar: veja o cabeçalho do arquivo.
- **Roteiro manual** para conferir pela interface: `docs/ROTEIRO_DE_TESTES.md`.
- **Ambiente em que foi testado:** Linux (Ubuntu 24.04), JDK 21 e MySQL Server 8.0. Por usar apenas Java padrão e caminhos relativos, o projeto não depende do sistema operacional; os scripts `executar.bat` (Windows) e `executar.sh` (Linux/macOS) usam os separadores corretos de cada ambiente.

## Decisões e limitações

- **Valores estimados:** distâncias, velocidades médias e emissões de CO2 são valores cadastrados e estimados. Não há navegação por GPS nem integração com serviços externos de mapas ou horários (fora do escopo da 1ª entrega).
- **Cálculo por tipo de transporte:** o carro considera um fator de congestionamento; o ônibus soma uma espera média de 5 minutos por trecho e usa emissão por passageiro; a bicicleta não emite CO2.
- **Histórico:** cada alternativa exibida em uma consulta de usuário logado é registrada como uma linha do histórico (mantemos as 50 mais recentes na tela).
- **Senhas:** guardadas com hash SHA-256, que já vem no Java. Em um sistema real, o recomendado seria um algoritmo próprio para senhas (como BCrypt), que exige biblioteca externa.
- **Sessões:** ficam na memória do servidor; ao reiniciar o programa, os usuários precisam entrar de novo.
- **Código de administrador:** valor padrão `movecity2026`, só para fins de demonstração acadêmica; em uso real deve ser trocado em `codigo.admin`.
- **Próximos passos possíveis:** integração com uma API de mapas para calcular distâncias reais, novos meios de transporte (caminhada, metrô) e testes automatizados em Java (JUnit).
