# 🚗 Desafio - Teste Prático Java

## 📋 Descrição

Este projeto consiste em uma API RESTful desenvolvida com Spring Boot para gerenciar vagas de estacionamento e reservas. A aplicação simula um sistema de locação onde usuários podem reservar, encerrar e consultar vagas disponíveis. 

O objetivo principal é aplicar boas práticas de desenvolvimento, arquitetura limpa e cobertura de testes automatizados, garantindo a manutenção da orientação a objetos sem o uso de anotações como `@lombok`.

---

## 🏗️ Arquitetura

A estrutura do projeto foi dividida em pacotes organizados por responsabilidades:

- **`configs`**: configurações gerais da aplicação, como segurança, CORS, beans e outras definições globais.
- **`controllers`**: camada responsável por receber as requisições HTTP e retornar as respostas adequadas.
- **`dtos`**: definidos como `records` para imutabilidade e simplicidade na transferência de dados entre camadas.
- **`entities`**: representam as tabelas do banco de dados. Não utilizam anotações do Lombok para manter a orientação a objetos pura.
- **`enums`**: tipos fixos da aplicação, como status de vaga.
- **`exceptions`**: exceções customizadas para tratar regras de negócio.
- **`handlers`**: captura e formata as exceções lançadas na aplicação.
- **`mappers`**: classes responsáveis por conversão entre entidades e DTOs.
- **`repositories`**: interfaces que interagem com o banco de dados via JPA.
- **`security`**: implementação da autenticação via JWT, filtros e configurações de segurança com Spring Security.
- **`services`**: contém a lógica de negócio da aplicação.
- **`specifications`**: filtros dinâmicos utilizando `Specification` do Spring Data.
- **`utils`**: funções auxiliares reutilizáveis.
- **`validators`**: validações customizadas de DTOs com regras de negócio específicas.

### 🛢️ Banco de Dados

O banco utilizado é **HSQLDB** (HyperSQL DataBase), funcionando localmente em memória durante a execução da aplicação. A comunicação com o banco é feita via **JPA (Java Persistence API)**.

---

## 📦 Entidades

### Estacionamento

```java
@Entity
@Table(name = "ESTACIONAMENTO")
public class Estacionamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double numero;
    private String tipo;
    private Double valorPorHora;
    
    @Enumerated(EnumType.STRING)
    private StatusVagaEnum status;
}
```

### Reserva

```java
@Entity
@Table(name = "RESERVA")
public class Reserva {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne @JoinColumn(name = "ESTACIONAMENTO_ID")
    private Estacionamento estacionamento;

    @ManyToOne @JoinColumn(name = "SOLICITANTE_ID")
    private Solicitante solicitante;

    private Timestamp dataInicio;
    private Timestamp dataFim;
    private Double valorTotal;
}
```

### Solicitante

```java
@Entity
@Table(name = "SOLICITANTE")
public class Solicitante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String cpf;
}
```

### Usuário

```java
@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String senha;

    @ManyToOne @JoinColumn(name = "SOLICITANTE_ID")
    private Solicitante solicitante;
}
```

---

## 📡 Principais Funcionalidades

### 1. Gerenciamento de Vagas
- Cadastro de vagas com número, tipo, valor por hora e status.
- Consulta de vagas com filtros e paginação.
- Atualização do status de uma vaga (DISPONIVEL, RESERVADA, OCUPADA).
- Exclusão de vagas.

### 2. Reserva e Locação
- Criação de reservas vinculadas a uma vaga e solicitante.
- Encerramento de reservas com cálculo automático do valor com base no tempo.
- Listagem de reservas com filtros e paginação.

### 3. Regras de Negócio
- Uma vaga reservada ou ocupada não pode ser reservada novamente.
- Reserva não pode ser encerrada mais de uma vez.
- Ao encerrar, a vaga volta ao status de disponível.
- Cálculo de valor total baseado na duração da reserva.

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/luizcordeirosn/teste-pratico-desenvolvedor-java.git
   cd teste-pratico-desenvolvedor-java
   ```

2. Compile e execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

3. Acesse o Swagger UI para testar os endpoints:
   [http://localhost:9292/swagger-ui/index.html](http://localhost:9292/swagger-ui/index.html)

---

## ✅ Testes e Execução

Foram implementados testes unitários para todos os métodos dos services utilizados pelos controllers, garantindo a confiabilidade da lógica de negócios. Os testes cobrem cenários de sucesso, falhas esperadas (como exceções) e validações de regras específicas, como:

- **EstacionamentoService**:
  - Criação com validação de vaga duplicada
  - Atualização de status da vaga
  - Exclusão com verificação de existência
  - Paginação e filtro

- **ReservaService**:
  - Agendamento de reserva com validações de data e status da vaga
  - Encerramento de reserva com atualização de status da vaga
  - Verificação de múltiplas reservas por solicitante
  - Paginação e filtro de reservas

- **SolicitanteService**:
  - Salvamento com validação de CPF duplicado
  - Exclusão com verificação de existência
  - Listagem paginada de solicitantes

Para rodar os testes unitários da aplicação:

```bash
mvn test
```

Todos os testes foram feitos utilizando **JUnit 5** e **Mockito**, com cobertura de exceções customizadas, uso de `@Mock` e `@InjectMocks`, e validação dos comportamentos esperados com `verify`.

---

## 📚 Documentação dos Endpoints

A API está documentada com Swagger. A documentação pode ser acessada em:

📌 [http://localhost:9292/swagger-ui/index.html](http://localhost:9292/swagger-ui/index.html)
