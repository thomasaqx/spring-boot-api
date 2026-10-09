# MedVoll API

API REST para gerenciamento de médicos de uma clínica fictícia (Voll Med), desenvolvida com **Spring Boot** e **MySQL**. Permite cadastrar, listar (com paginação), detalhar, atualizar e inativar médicos. O acesso é protegido por **autenticação stateless com token JWT**.

## Tecnologias

- **Java 21**
- **Spring Boot 4** (Web MVC, Data JPA, Validation, Security, DevTools)
- **MySQL 8**
- **Flyway** — versionamento do banco de dados via migrations
- **Spring Security** + **java-jwt (auth0)** — autenticação e controle de acesso com JWT
- **Lombok** — redução de código boilerplate nas entidades
- **Maven** (com Maven Wrapper)

## Estrutura do projeto

```
src/main/java/com/github/thomasaqx/MedVoll
├── controller        → endpoints REST (MedicoController, AuthController)
├── domain            → entidades JPA e regras de negócio
│   ├── medico        → Medico, Especialidade
│   ├── endereco      → Endereco (embutido em Medico)
│   └── usuario       → Usuario (implementa UserDetails)
├── dto               → objetos de entrada/saída da API (records)
│   ├── medico        → DTOCadastroMedico, DTOAtualizacaoMedico, DTOListagemMedico, DTODetalhamentoMedico
│   ├── endereco      → DTOEndereco
│   ├── usuario       → DTOUsuario
│   └── auth          → DTOAuth, DTOTokenJWT
├── interfaces        → repositórios Spring Data (MedicoInterface, UsuarioInterface)
├── service           → AuthService (carrega o usuário), TokenService (gera e valida o JWT)
└── Infra
    ├── exception     → TratamentoDeErros (respostas padronizadas de erro)
    └── security      → ConfigurationsSecurity (regras de acesso), FilterSecurity (lê e valida o token)

src/main/resources
├── application.yaml  → configurações da aplicação, do banco e do JWT
└── db/migration      → migrations do Flyway (V1, V2, ...)
```

## Como executar

### Pré-requisitos

- JDK 21
- MySQL 8 rodando em `localhost:3306` (ou Docker)

### 1. Subir o banco de dados

Com Docker, **na primeira vez** (cria o container):

```bash
docker run -d --name mysql-medvoll -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=vollmed_api mysql:8
```

**Nas próximas vezes**, o container já existe — basta iniciá-lo (rodar o `docker run` de novo dá erro de nome em uso):

```bash
docker start mysql-medvoll
```

O banco `vollmed_api` também é criado automaticamente na primeira execução da aplicação, caso não exista.

### 2. Configurar variáveis de ambiente (opcional)

| Variável      | Padrão     | Descrição                                   |
|---------------|------------|---------------------------------------------|
| `DB_USERNAME` | `root`     | Usuário do MySQL                            |
| `DB_PASSWORD` | `root`     | Senha do MySQL                              |
| `JWT_SECRET`  | `12345678` | Chave usada para assinar e validar os tokens JWT |

> O valor padrão de `JWT_SECRET` serve apenas para desenvolvimento. Em qualquer outro ambiente, defina uma chave longa e aleatória (32+ caracteres) — quem conhece a chave consegue gerar tokens válidos.

### 3. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`. As tabelas são criadas automaticamente pelo Flyway ao iniciar.

### 4. Criar um usuário para login

As senhas são armazenadas como hash **BCrypt** (nunca em texto puro). Exemplo de usuário com a senha `123456`:

```sql
INSERT INTO usuarios (login, senha) VALUES ('admin@voll.med', '$2a$10$.RUHIFVDU9EPR5IApGRI6.q6o/9yCZhUF5hdYRVNZWiKTCqaLLSse');
```

## Autenticação

Todas as rotas exigem um **token JWT**, exceto o `POST /login`.

### Fluxo

1. O cliente envia login e senha para `POST /login`.
2. A API confere as credenciais (senha comparada com o hash BCrypt) e devolve um token JWT assinado com HMAC256.
3. Nas demais requisições, o cliente envia o token no header:
   ```http
   Authorization: Bearer <token>
   ```
4. O `FilterSecurity` intercepta cada requisição, valida o token (assinatura, emissor `API Voll.med` e expiração) e autentica o usuário dono dele.
5. O `ConfigurationsSecurity` libera a rota se o usuário estiver autenticado.

A API é **stateless**: não guarda sessão nem usa cookies — cada requisição se identifica sozinha pelo token.

### Respostas de segurança

| Situação                                  | Status             |
|-------------------------------------------|--------------------|
| Login com credenciais corretas            | `200 OK` + token   |
| Login com usuário ou senha inválidos      | `401 Unauthorized` |
| Login com campos em branco                | `400 Bad Request`  |
| Rota protegida sem token                  | `403 Forbidden`    |
| Rota protegida com token inválido/expirado| `403 Forbidden`    |

### Usando no Bruno / Postman / Insomnia

1. Faça o `POST /login` e copie o valor de `token` da resposta.
2. Nas outras requisições, em **Auth**, escolha **Bearer Token** e cole o token.

O token expira em **2 horas**; depois disso, faça login de novo.

## Endpoints

| Método   | Rota            | Autenticação | Descrição                                  |
|----------|-----------------|--------------|--------------------------------------------|
| `POST`   | `/login`        | pública      | Autentica o usuário e devolve um token JWT |
| `POST`   | `/medicos`      | token        | Cadastra um novo médico                    |
| `GET`    | `/medicos`      | token        | Lista médicos ativos (paginado)            |
| `GET`    | `/medicos/{id}` | token        | Detalha um médico                          |
| `PUT`    | `/medicos`      | token        | Atualiza nome, telefone e/ou endereço      |
| `DELETE` | `/medicos/{id}` | token        | Inativa um médico (exclusão lógica)        |

Todas as requisições com corpo devem enviar o header `Content-Type: application/json`.

---

### POST — Login

**Requisição**

```http
POST http://localhost:8080/login
Content-Type: application/json
```

```json
{
  "email": "admin@voll.med",
  "senha": "123456"
}
```

**Resposta:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### POST — Cadastrar médico

**Requisição**

```http
POST http://localhost:8080/medicos
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "nome": "michael jackson",
  "email": "michael.jack@voll.med",
  "telefone": "1362212213",
  "crm": "132233",
  "especialidade": "ORTOPEDIA",
  "endereco": {
    "logradouro": "rua 1",
    "bairro": "bairro",
    "cep": "12345678",
    "cidade": "Brasilia",
    "uf": "DF",
    "numero": "1",
    "complemento": "complemento"
  }
}
```

**Resposta:** `201 Created`, com o header `Location: http://localhost:8080/medicos/{id}` e o médico criado no corpo:

```json
{
  "id": 8,
  "nome": "michael jackson",
  "email": "michael.jack@voll.med",
  "crm": "132233",
  "especialidade": "ORTOPEDIA",
  "endereco": {
    "logradouro": "rua 1",
    "bairro": "bairro",
    "cep": "12345678",
    "numero": "1",
    "complemento": "complemento",
    "uf": "DF",
    "cidade": "Brasilia"
  }
}
```

**Validações**

| Campo                  | Regra                                                              |
|------------------------|--------------------------------------------------------------------|
| `nome`                 | obrigatório                                                        |
| `email`                | obrigatório, formato de e-mail válido                              |
| `telefone`             | obrigatório                                                        |
| `crm`                  | obrigatório, de 4 a 6 dígitos                                      |
| `especialidade`        | obrigatório: `ORTOPEDIA`, `CARDIOLOGIA`, `GINECOLOGIA` ou `DERMATOLOGIA` |
| `endereco`             | obrigatório                                                        |
| `endereco.cep`         | obrigatório, 8 dígitos (somente números)                           |
| demais campos de `endereco` | obrigatórios                                                  |

Se alguma validação falhar, a API responde `400 Bad Request` listando só os campos inválidos:

```json
[
  { "campo": "crm", "mensagem": "deve corresponder a \"\\d{4,6}\"" },
  { "campo": "email", "mensagem": "deve ser um endereço de e-mail bem formado" }
]
```

---

### GET — Listar médicos

Retorna apenas médicos **ativos**, ordenados por nome, 10 por página.

**Requisição**

```http
GET http://localhost:8080/medicos
Authorization: Bearer <token>
```

**Resposta:** `200 OK`

```json
{
  "content": [
    {
      "id": 8,
      "nome": "michael jackson",
      "email": "michael.jack@voll.med",
      "crm": "132233",
      "especialidade": "ORTOPEDIA"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "empty": false, "sorted": true, "unsorted": false },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

**Paginação e ordenação (opcionais)**

| Parâmetro | Exemplo           | Descrição                         |
|-----------|-------------------|-----------------------------------|
| `page`    | `?page=1`         | Número da página (começa em 0)    |
| `size`    | `?size=5`         | Quantidade de itens por página    |
| `sort`    | `?sort=crm,desc`  | Campo e direção da ordenação      |

Exemplos:

```http
GET http://localhost:8080/medicos?size=5&page=1
```

```http
GET http://localhost:8080/medicos?sort=crm,desc
```

---

### GET — Detalhar médico

O `id` vai na URL.

**Requisição**

```http
GET http://localhost:8080/medicos/8
Authorization: Bearer <token>
```

**Resposta:** `200 OK`, com os dados completos do médico (mesmo formato da resposta do cadastro). Um `id` inexistente retorna `404 Not Found`.

---

### PUT — Atualizar médico

O `id` é **obrigatório** e vai no corpo da requisição. Os campos atualizáveis são `nome`, `telefone` e `endereco`; os que não forem enviados permanecem como estão.

**Requisição — atualização completa**

```http
PUT http://localhost:8080/medicos
Content-Type: application/json
Authorization: Bearer <token>
```

```json
{
  "id": 8,
  "nome": "Michael Jackson Silva",
  "telefone": "1399998888",
  "endereco": {
    "logradouro": "rua 2",
    "bairro": "centro",
    "cep": "87654321",
    "cidade": "Santos",
    "uf": "SP",
    "numero": "200",
    "complemento": "apto 12"
  }
}
```

**Requisição — atualização parcial** (só o nome)

```json
{
  "id": 8,
  "nome": "Michael Jackson Silva"
}
```

**Resposta:** `200 OK`, com os dados completos do médico já atualizados.

> `email`, `crm` e `especialidade` não são alterados pelo PUT.

---

### DELETE — Inativar médico

O `id` vai **na URL** (não no corpo). O registro não é apagado do banco: o médico é marcado como inativo (`ativo = false`) e deixa de aparecer na listagem do `GET`.

**Requisição**

```http
DELETE http://localhost:8080/medicos/8
Authorization: Bearer <token>
```

**Resposta:** `204 No Content`

> Enviar `DELETE http://localhost:8080/medicos` sem o id resulta em `405 Method Not Allowed`.

---

## Banco de dados

O schema é versionado com **Flyway** em `src/main/resources/db/migration`:

| Migration | Descrição                                           |
|-----------|-----------------------------------------------------|
| V1        | Cria a tabela `medicos`                             |
| V2        | Adiciona a coluna `telefone`                        |
| V3        | Adiciona a coluna `ativo`                           |
| V4        | Ajusta o tipo da coluna `ativo` para `BIT`          |
| V5        | Cria a tabela `usuarios`                            |
| V6        | Renomeia a coluna `nome` de `usuarios` para `login` |

Convenções importantes:
- O nome do arquivo deve seguir o padrão `V<número>__<descrição>.sql` (**V maiúsculo** e **dois underscores**)
- Nunca altere uma migration já aplicada — crie uma nova versão
- O Hibernate está configurado com `ddl-auto: validate`, ou seja, só valida se as entidades batem com as tabelas; quem altera o schema é sempre o Flyway

## Próximos passos

- Responder `401 Unauthorized` (em vez de `403`) para requisições sem token ou com token inválido, configurando um `authenticationEntryPoint`
- Endpoint para cadastro de usuários (hoje o usuário é inserido direto no banco)
- Perfis de acesso (roles) para restringir operações por tipo de usuário
