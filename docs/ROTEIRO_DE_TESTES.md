# Roteiro de testes manuais – MoveCity

Passo a passo para conferir, pela interface, que o sistema atende aos requisitos (RQ) e às regras de negócio (RN).
Pré-requisito: sistema em execução (veja o README, seção "Como executar") com o banco criado e os dados de exemplo carregados.
Endereço: http://localhost:8080

Usuários de demonstração: `admin@movecity.com` / `admin123` (administrador) e `passageiro@movecity.com` / `senha123` (passageiro).

| # | Passo | Resultado esperado | Cobre |
|---|---|---|---|
| 1 | Sem entrar, abrir **Consultar rotas** e buscar origem `Centro`, destino `Zona Norte` | 3 alternativas, cada uma com trechos, tempo, distância e impacto. A alternativa **Ônibus + bicicleta** aparece com a etiqueta *Mais sustentável* (630 g CO₂) e **De carro, direto** com *Mais rápida* (31 min) | RQ05, RQ06, RQ07, RN05, RN06 |
| 2 | Buscar origem `Terminal`, destino `Aeroporto` | Apenas 1 alternativa, **sem** etiqueta de destaque | RQ07 |
| 3 | Buscar origem `Narnia`, destino `Mordor` | Mensagem informando que não há rotas para o trajeto | RQ05 |
| 4 | Em **Criar conta**, cadastrar passageiro com e-mail novo | Mensagem de sucesso e redirecionamento para *Entrar* | RQ01 |
| 5 | Tentar cadastrar de novo com o mesmo e-mail (inclusive trocando maiúsculas por minúsculas) | Erro "Já existe um usuário cadastrado com este e-mail." | RN01 |
| 6 | Tentar cadastrar perfil **Administrador** com código errado | Erro "Código de administrador inválido." | RN03 |
| 7 | Entrar com senha errada | Erro "E-mail ou senha inválidos." | RQ02 |
| 8 | Entrar como `passageiro@movecity.com` | Menu mostra Favoritos e Histórico; **não** mostra Administração | RQ02, RN03 |
| 9 | Repetir a consulta do passo 1 logado | Aviso de que a consulta foi registrada no histórico | RQ09, RN07 |
| 10 | Abrir **Histórico** | 3 linhas (uma por alternativa consultada) com data e hora | RQ09 |
| 11 | Na consulta, clicar em **Salvar como favorita** em uma alternativa | Mensagem de sucesso | RQ08 |
| 12 | Clicar de novo na mesma alternativa | Erro "Esta rota já está nos seus favoritos." | RN08 |
| 13 | Abrir **Favoritos** e remover a rota | A rota some da lista | RQ08 |
| 14 | Sair e entrar como `admin@movecity.com` | Menu mostra **Administração** | RN03 |
| 15 | Administração > Rotas: criar rota `Bairro A` → `Bairro B` com 2 trechos (ônibus 8 km + bicicleta 2 km) | Rota aparece na lista com totais calculados | RQ03 (Create/Read), RN05 |
| 16 | Tentar salvar uma rota sem nenhum trecho (remover a linha do trecho) | Erro "A rota precisa ter pelo menos um trecho." | RN04 |
| 17 | Clicar em **Editar** na rota criada, trocar o destino e salvar | Lista mostra o novo destino | RQ03 (Update) |
| 18 | Administração > Transportes: criar um ônibus novo; depois editar a velocidade | Lista atualizada | RQ04 |
| 19 | Tentar excluir o transporte **Automóvel particular** (usado em rotas) | Erro informando que o transporte está em uso | RQ04 |
| 20 | Administração > Alertas: registrar um alerta para a rota `Centro → Zona Norte (Ônibus + bicicleta)` | Alerta aparece na lista como *Ativo* | RQ10 |
| 21 | Sair e consultar `Centro` → `Zona Norte` | A alternativa recebe o aviso do alerta | RQ10 |
| 22 | Como administrador, **Desativar** o alerta e repetir a consulta | O aviso não aparece mais | RN09 |
| 23 | Em Administração > Rotas, **Excluir** a rota criada no passo 15 | Rota some da lista (os trechos são excluídos junto) | RQ03 (Delete), RN04 |
| 24 | Parar o sistema (`Ctrl+C`), iniciar de novo e consultar `Centro` → `Zona Norte` | Os dados continuam lá (persistência no banco) | Persistência em BD |

## Conferência dos cálculos (exemplo `Centro → Zona Norte`)

Valores dos transportes do `schema.sql`: carro 30 km/h, 120 g CO₂/km, fator de congestionamento 1,30; ônibus 20 km/h, 70 g CO₂/km, espera de 5 min; bicicleta 15 km/h, 0 g CO₂/km.

| Alternativa | Tempo | Distância | Impacto |
|---|---|---|---|
| De carro, direto (12 km) | 12 ÷ 30 × 60 × 1,30 = **31,2 min** | 12 km | 12 × 120 = **1.440 g** |
| De ônibus, direto (13 km) | 13 ÷ 20 × 60 + 5 = **44 min** | 13 km | 13 × 70 = **910 g** |
| Ônibus 9 km + bicicleta 3 km | (9 ÷ 20 × 60 + 5) + (3 ÷ 15 × 60) = 32 + 12 = **44 min** | 12 km | 9 × 70 + 0 = **630 g** |

## Teste automatizado

`docs/testes/teste_api.py` repete e amplia estes passos de forma automática (66 verificações). As instruções de uso estão no cabeçalho do arquivo.
