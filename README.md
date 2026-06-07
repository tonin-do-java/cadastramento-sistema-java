# Sistema de Cadastro e Login de Usuários

API REST desenvolvida com Java e Spring Boot para gerenciamento de usuários, autenticação e autorização utilizando JWT (JSON Web Token).

## Sobre o Projeto

Este projeto foi criado com o objetivo de aplicar conceitos fundamentais do desenvolvimento backend moderno, incluindo autenticação segura, criptografia de senhas, controle de acesso e persistência de dados.

A aplicação permite o cadastro, autenticação e gerenciamento de usuários por meio de uma API REST documentada com Swagger.

## Tecnologias Utilizadas

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT (JSON Web Token)
* BCrypt
* MySQL
* Maven
* Swagger / OpenAPI
* jUnit 5

## Funcionalidades

### Autenticação

* Login de usuários
* Geração de token JWT
* Validação de token
* Proteção de rotas com Spring Security

### Usuários

* Cadastro de usuários
* Listagem de usuários
* Busca de usuário por ID
* Atualização de cadastro
* Exclusão de usuários

### Segurança

* Criptografia de senhas com BCrypt
* Controle de acesso baseado em perfis (ADMIN e USER)
* Tratamento global de exceções
* Rotas protegidas por autenticação JWT

## Estrutura do Projeto

```text
src/main/java
├── controller
├── service
├── repository
├── model
├── dto
├── security
├── exception
└── config
```

## Como Executar

### Pré-requisitos

* Java 21
* Maven
* PostgreSQL
* SpringBoot 3.x

### Passos

1. Clone o repositório

```bash
git clone <url-do-repositorio>
```

2. Configure o banco de dados no arquivo:

```properties
application.properties
```

3. Execute a aplicação:

```bash
mvn spring-boot:run
```

4. Acesse a documentação Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Endpoints Principais

### Autenticação

POST /login

Realiza autenticação do usuário e retorna um token JWT.

### Usuários

POST /usuarios/cadastrar

GET /usuarios/listarTodos

GET /usuarios/listarPorId/{id}

PUT /usuarios/atualizarCadastro/{id}

DELETE /usuarios/deletarUsuario

## Objetivos de Aprendizado

Durante o desenvolvimento deste projeto foram aplicados conceitos de:

* APIs REST
* Arquitetura em camadas
* Spring Security
* JWT
* Persistência de dados com JPA/Hibernate
* Boas práticas de desenvolvimento backend
* Tratamento de exceções
* Testes automatizados

## Melhorias Futuras

* Refresh Token
* Paginação
* Recuperação de senha
* Confirmação por e-mail
* Docker
* Deploy em nuvem
* Pipeline CI/CD

## Autor

Antônio Fernandes dos Santos

Estudante de Ciência da Computação com foco em desenvolvimento Backend utilizando Java, Spring Boot e bancos de dados relacionais.
