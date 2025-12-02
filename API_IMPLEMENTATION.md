# Integração de API no aplicativo RunUnit

Este documento resume como o aplicativo Android passou a consumir os endpoints REST do back-end conforme o guia fornecido. As chamadas utilizam Retrofit com autenticação via JWT.

## Autenticação
- **Endpoint:** `POST /api/auth/login`
- **Implementação:** `LoginActivity` envia `LoginRequest` e, ao obter `LoginResponse`, persiste o token e metadados via `SessionManager`. Usuários autenticados são redirecionados diretamente para o dashboard.

## Cadastro
- **Endpoint:** `POST /api/auth/register`
- **Implementação:** `RegisterActivity` envia `RegisterRequest` com dados básicos e, em seguida, executa login automático reutilizando o mesmo e-mail/senha para obter o token JWT.

## Sessões de Corrida
- **Criar sessão**
  - **Endpoint:** `POST /api/sessions`
  - **Implementação:** ao finalizar uma corrida em `RunTrackerActivity`, os dados coletados (duração, distância, pace e horário de início) são enviados como `RunningSessionRequest`. A chamada utiliza o token salvo pelo `SessionManager`.
- **Listar sessões**
  - **Endpoint:** `GET /api/sessions`
  - **Implementação:** `DashboardActivity` consulta as sessões do usuário autenticado, converte as respostas em `RunData` para popular os cards e armazena os resultados em cache local.

## Camada de Rede
- **Cliente:** `ApiClient` configura Retrofit com conversor Gson, `HttpLoggingInterceptor` e `AuthInterceptor` para anexar `Authorization: Bearer <token>` automaticamente.
- **Gerenciamento de sessão:** `SessionManager` centraliza persistência do token, nome e e-mail do usuário nas `SharedPreferences`.

## Como alterar o servidor
O endpoint base está definido em `ApiClient` (`BASE_URL`). Basta alterar esse valor para apontar para o ambiente desejado (ex.: desenvolvimento, homologação ou produção).
