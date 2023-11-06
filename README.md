<div align="center">

# Todo List

[![build](https://github.com/FerroEduardo/micronaut-todo/actions/workflows/build.yaml/badge.svg)](https://github.com/FerroEduardo/micronaut-todo/actions/workflows/build.yaml)
[![test](https://github.com/FerroEduardo/micronaut-todo/actions/workflows/test.yaml/badge.svg)](https://github.com/FerroEduardo/micronaut-todo/actions/workflows/test.yaml)
</div>

| Description       | Method | URL                             | Body                                             |
|:------------------|:------:|:--------------------------------|:-------------------------------------------------|
| Sign-in           |  GET   | /auth/signin                    | `{ "username": "eduardo", "password": "senha" }` |
| Sign-up           |  POST  | /auth/signip                    | `{ "username": "eduardo", "password": "senha" }` |
| Index todo        |  GET   | /todo                           |                                                  |
| Show todo         |  GET   | /todo/:todoId                   |                                                  |
| Create todo       |  POST  | /todo                           | `{ "description": "abcd" }`                      |
| Update todo       |  PUT   | /todo/:todoId                   | `{ "description": "abcd", "completed": false }`  |
| Delete todo       | DELETE | /todo/:todoId                   |                                                  |
| Set todo complete |  POST  | /todo/:todoId/complete/:boolean |                                                  |

> Swagger: `/swagger/views/swagger-ui`