# MoveCity – Aplicação Web para Mobilidade Urbana Sustentável

**AEP – 4º Semestre (2026.2) | Engenharia de Software / Análise e Desenvolvimento de Sistemas | UniCesumar**

Repositório público: https://github.com/Arthurtanaka/AEP-ESOFT4S

## Equipe

| Integrante | RA |
|---|---|
| Miguel Souza Colis | 25014158-2 |
| Arthur Riki Farias Tanaka | 25224963-2 |
| João Gustavo Zanoni Gonçalves | 25357434-2 |

## Sobre o projeto

O MoveCity é uma aplicação web de mobilidade urbana sustentável. O usuário informa uma origem e um destino, consulta as alternativas de deslocamento cadastradas (rotas formadas por trechos, cada trecho com um meio de transporte) e compara as opções por **tempo, distância e impacto ambiental**, com destaque para a alternativa mais sustentável.

### Problema (descoberta)

Congestionamentos e transporte urbano pouco eficiente: excesso de carros particulares, falta de integração entre os meios de transporte, informações dispersas e baixa previsibilidade levam a engarrafamentos, poluição do ar, maior tempo de deslocamento e perda de qualidade de vida.

### ODS vinculados

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

- **Linguagem:** Java (orientação a objetos: herança, polimorfismo, composição, classe abstrata).
- **Banco de dados:** MySQL 8.0 ou superior, acessado por JDBC.
- **Arquitetura em camadas:** Model → DAO → Service → Controller.
- **Interface:** HTML5, CSS3 e JavaScript.

## Estrutura do repositório

```
AEP-ESOFT4S/
├── src/                      Código-fonte Java (model, dao, service, controller, util)
├── docs/                     PDF da 1ª entrega e diagramas (classes e DER)
├── database/
│   └── schema.sql            Script de criação das tabelas
├── README.md
└── .gitignore
```

## Como executar

> As instruções abaixo serão confirmadas e detalhadas na 2ª entrega, junto com o código-fonte.

**Pré-requisitos (Windows, Linux ou macOS):**

1. JDK (Java Development Kit) instalado.
2. MySQL 8.0 ou superior instalado e em execução.

**Passos:**

1. Clone o repositório: `git clone https://github.com/Arthurtanaka/AEP-ESOFT4S.git`
2. Crie o banco de dados executando o script: `mysql -u root -p < database/schema.sql`
3. Configure o usuário e a senha do MySQL no arquivo de conexão do projeto (classe de conexão do pacote `dao`).
4. Compile e execute a aplicação a partir da pasta `src` (ou pela IDE de sua preferência).
