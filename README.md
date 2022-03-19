# Desafio CWI SICREDI

## Algumas decições:
- Foi utilizado a própria entidade devido ser uma aplicação simples e não acabar comprometendo a performance convertendo entidades para DTOs sem necessidade.
- Foi utilizado banco de dados relacional devido ter relacionamento entre as entidades. O motivo de ter sido o PostgreSQL é devido eu ter mais conhecimento nele.
- Foi utilizado o swagger para realizar a documentação das rotas devido ele promover automações e outras facilidades como a geração da interface para visualizar e testar as rotas pela interface gráfica.
- Foi escolhido o RabbitMQ para envio de mensagens pelo motivo de ser o que eu tenho conhecimento no momento.
- Foi utilizado o JUnit 5 para a realização de testes unitários.

## Observações para rodar a aplicação localmente:
- Ter o Java 11+ instalado na máquina.
- Ter o RabbitMQ rodando na sua máquina. Seja no docker ou no próprio sistema operacional. (Pesquisar no google como instalar/configurar)
- Alterar as seguintes propriedades no arquivo application-dev.properties:
  1 - spring.datasource.url (colocar a url local da sua máquina);
  2 - spring.rabbitmq.host (colocar o host da sua máquina, geralmente é "localhost");
  3 - spring.rabbitmq.username (colocar o usuário que foi configurado para acessar o RabbitMQ)
  4 - spring.rabbitmq.password (colocar a senha que foi configurada para acessar o RabbitMQ)

## Deploy da aplicação no Heroku
- Link da API:  https://desafio-cwi-sicredi.herokuapp.com/api/v1
- Link da documentação das rotas da API no swagger: https://desafio-cwi-sicredi.herokuapp.com/api/v1/swagger-ui.html
