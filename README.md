# MedVoll API

API REST para gerenciamento de médicos de uma clínica fictícia (Voll Med), desenvolvida com **Spring Boot** e **MySQL**. Permite cadastrar, listar (com paginação), atualizar e inativar médicos.

## Tecnologias

- **Java 21**
- **Spring Boot 4** (Web MVC, Data JPA, Validation, DevTools)
- **MySQL 8**
- **Flyway** — versionamento do banco de dados via migrations
- **Lombok** — redução de código boilerplate nas entidades
- **Maven** (com Maven Wrapper)

## Estrutura do projeto

```
src/main/java/com/github/thomasaqx/MedVoll
├── controller        → endpoints REST (MedicoController)
├── domain            → entidades JPA e regras de negócio
│   ├── medico        → Medico, Especialidade
│   └── endereco      → Endereco (embutido em Medico)
├── dto               → objetos de entrada/saída da API (records)
│   ├── medico        → DTOCadastroMedico, DTOAtualizacaoMedico, DTOListagemMedico
│   └── endereco      → DTOEndereco
└── repository        → acesso ao banco (MedicoRepository)

src/main/resources
├── application.yaml  → configurações da aplicação e do banco
└── db/migration      → migrations do Flyway (V1, V2, ...)
```

## Como executar

### Pré-requisitos

- JDK 21
- MySQL 8 rodando em `localhost:3306` (ou Docker)

### 1. Subir o banco de dados

Com Docker:

```bash
docker run -d --name mysql-medvoll -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=vollmed_api mysql:8
```

Para subir o mesmo container: 

```bash
docker start mysql-medvoll
```
Desligar o container:
```bash
docker stop mysql-medvoll
```
Inicia-lo automaticamente com o Docker Desktop: 
```bash
docker update --restart unless-stopped mysql-medvoll
```

O banco `vollmed_api` também é criado automaticamente na primeira execução, caso não exista.

### 2. Configurar credenciais (opcional)

Por padrão a aplicação usa usuário `root` e senha `root`. Para usar outras credenciais, defina as variáveis de ambiente:

| Variável      | Padrão |
|---------------|--------|
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | `root` |

### 3. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`. As tabelas são criadas automaticamente pelo Flyway ao iniciar.

## Endpoints

Base: `http://localhost:8080/medicos`

| Método   | Rota            | Descrição                                       |
|----------|-----------------|-------------------------------------------------|
| `POST`   | `/medicos`      | Cadastra um novo médico                         |
| `GET`    | `/medicos`      | Lista médicos ativos (paginado)                 |
| `PUT`    | `/medicos`      | Atualiza nome, telefone e/ou endereço           |
| `DELETE` | `/medicos/{id}` | Inativa um médico (exclusão lógica)             |

Todas as requisições com corpo devem enviar o header `Content-Type: application/json`.

---

### POST — Cadastrar médico

**Requisição**

```http
POST http://localhost:8080/medicos
Content-Type: application/json
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

**Resposta:** `200 OK` (sem corpo)

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

Se alguma validação falhar, a API responde `400 Bad Request`.

---

### GET — Listar médicos

Retorna apenas médicos **ativos**, ordenados por nome, 10 por página.

**Requisição**

```http
GET http://localhost:8080/medicos
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
    },
    {
      "id": 4,
      "nome": "Rodrigo Ferreira Santos",
      "email": "rodrigo.ferreira@voll.med",
      "crm": "123456",
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
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 2,
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

### PUT — Atualizar médico

O `id` é **obrigatório** e vai no corpo da requisição. Os campos atualizáveis são `nome`, `telefone` e `endereco`; os que não forem enviados permanecem como estão.

**Requisição — atualização completa**

```http
PUT http://localhost:8080/medicos
Content-Type: application/json
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

**Resposta:** `200 OK` (sem corpo)

> `email`, `crm` e `especialidade` não são alterados pelo PUT.

---

### DELETE — Inativar médico

O `id` vai **na URL** (não no corpo). O registro não é apagado do banco: o médico é marcado como inativo (`ativo = false`) e deixa de aparecer na listagem do `GET`.

**Requisição**

```http
DELETE http://localhost:8080/medicos/8
```

**Resposta:** `200 OK` (sem corpo)

> Enviar `DELETE http://localhost:8080/medicos` sem o id resulta em `405 Method Not Allowed`.

---

## Banco de dados

O schema é versionado com **Flyway** em `src/main/resources/db/migration`:

| Migration | Descrição                                   |
|-----------|---------------------------------------------|
| V1        | Cria a tabela `medicos`                     |
| V2        | Adiciona a coluna `telefone`                |
| V3        | Adiciona a coluna `ativo`                   |
| V4        | Ajusta o tipo da coluna `ativo` para `BIT`  |

Convenções importantes:
- O nome do arquivo deve seguir o padrão `V<número>__<descrição>.sql` (**V maiúsculo** e **dois underscores**)
- Nunca altere uma migration já aplicada — crie uma nova versão
- O Hibernate está configurado com `ddl-auto: validate`, ou seja, só valida se as entidades batem com as tabelas; quem altera o schema é sempre o Flyway
