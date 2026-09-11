# MoveCity — Aplicação Web para Mobilidade Urbana Sustentável

Atividade de Estudo Programada (AEP) — 4º Semestre (2026.2)
Engenharia de Software | Análise e Desenvolvimento de Sistemas — UniCesumar

**ODS Vinculada:** ODS 11 — Cidades e Comunidades Sustentáveis

**Equipe de Desenvolvimento:**
- Miguel Souza Colis — RA 25014158-2
- Arthur Riki Farias Tanaka — RA 25224963-2
- João Gustavo Zanoni Gonçalves — RA 25357434-2

## Sobre o projeto

O MoveCity é uma aplicação web de mobilidade urbana sustentável. O sistema permite informar origem e destino, consultar alternativas de deslocamento e comparar opções de transporte, apresentando informações de forma centralizada e destacando alternativas mais sustentáveis — em alinhamento ao ODS 11.

O documento completo da 1ª entrega (Descoberta, Concepção, Justificativa Técnica, Diagrama de Classe e DER) está em [`/docs/AEP_MoveCity_1a_Entrega.pdf`](./docs/AEP_MoveCity_1a_Entrega.pdf).

## Estrutura do repositório

```
/src        → código-fonte Java (pacotes model, dao, service e util)
/docs       → documento da entrega (PDF) e diagramas de classe e banco de dados
/database   → script SQL de criação das tabelas (schema.sql)
.gitignore  → arquivos e diretórios que não devem ser versionados
```

## Lista de Requisitos

| Requisito | Descrição |
|---|---|
| RF01 | O sistema deve permitir o cadastro de usuários, com nome, e-mail e senha. |
| RF02 | O sistema deve permitir informar uma origem e um destino para realizar uma consulta de rota. |
| RF03 | O sistema deve apresentar diferentes alternativas de transporte para o deslocamento. |
| RF04 | O sistema deve permitir comparar alternativas considerando tempo, distância e impacto ambiental. |
| RF05 | O sistema deve destacar opções de transporte mais sustentáveis quando houver alternativas disponíveis. |
| RF06 | O sistema deve permitir ao usuário salvar uma rota como favorita. |
| RF07 | O sistema deve manter um histórico das consultas de rotas realizadas por usuários autenticados. |
| RF08 | O sistema deve permitir a exibição de alertas e informações relevantes sobre o deslocamento. |

**Requisitos não funcionais:** usabilidade, responsividade, segurança, desempenho, disponibilidade e manutenibilidade.

## Cronograma de Execução (1ª Entrega — 27/08/2026 a 11/09/2026)

| Data | Atividade | Responsável |
|---|---|---|
| 27/08/2026 | Levantamento de requisitos e definição do escopo | Todos os integrantes |
| 31/08/2026 | Modelagem do Diagrama de Classe e do DER | Miguel Souza Colis |
| 03/09/2026 | Criação e estruturação do repositório no GitHub | Arthur Riki Farias Tanaka |
| 06/09/2026 | Redação da Justificativa Técnica e Concepção do projeto | João Gustavo Zanoni Gonçalves |
| 09/09/2026 | Revisão geral do documento e formatação do PDF | Todos os integrantes |
| 11/09/2026 | Entrega final da 1ª etapa (PDF + link do GitHub) | Todos os integrantes |

## Arquitetura e Tecnologias

- **Linguagem:** Java (POO — herança, composição e polimorfismo)
- **Banco de Dados:** MySQL
- **Padrão Arquitetural:** Arquitetura em camadas (Model → DAO/Repository → Service)

Mais detalhes e a justificativa completa dessas escolhas estão no documento da 1ª entrega, em `/docs`.
