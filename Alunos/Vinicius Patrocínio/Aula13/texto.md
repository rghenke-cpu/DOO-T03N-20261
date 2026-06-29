# Aula 13 - Conceitos não vistos em aula

### Nome: Vinícius Patrocínio

---

## 1° Conceito escolhido --> DSL (Domain Specific Language)

### Timestamp do vídeo
03:31

### O que é?
DSL (Domain Specific Language), ou Linguagem Específica de Domínio, é uma linguagem criada para resolver problemas de uma área específica. Diferente de linguagens de propósito geral, como Java ou Kotlin, uma DSL possui comandos voltados para uma finalidade determinada.

### Para que serve?
Ela serve para tornar o código mais simples, legível e fácil de escrever em um contexto específico, reduzindo a complexidade e aumentando a produtividade do desenvolvedor.

### Como é normalmente utilizada?
É muito utilizada em ferramentas de configuração, consultas a banco de dados e frameworks. No ecossistema Kotlin, um exemplo bastante conhecido é o *Gradle Kotlin DSL*, usado para configurar projetos de forma mais intuitiva.

### Exemplo de código

kotlin
dependencies {
implementation("org.springframework.boot:spring-boot-starter-web")
testImplementation("org.junit.jupiter:junit-jupiter")
}


Nesse exemplo, a DSL do Gradle permite configurar as dependências do projeto de maneira simples e organizada.

---

## 2° Conceito escolhido --> Sealed Class 

### Timestamp do vídeo
01:50 

### O que é?
Uma Sealed Class é um tipo especial de classe que permite controlar quais outras classes podem herdar dela. Diferentemente de uma classe comum, a herança é restrita a um conjunto específico de subclasses.

### Para que serve?
Ela serve para tornar o código mais seguro e organizado, garantindo que apenas as classes autorizadas possam estender a classe principal. É muito utilizada para representar estados de uma aplicação, respostas de APIs e diferentes tipos de resultados. Diferentemente de uma classe final, que impede qualquer herança, a sealed class permite herança, mas apenas para um conjunto limitado de subclasses. Isso torna o código mais previsível e facilita o tratamento de todos os casos possíveis.

### Como é normalmente utilizada?
A classe é declarada com a palavra-chave sealed. Em Kotlin, as subclasses devem seguir as regras da linguagem para poder herdar dessa classe, permitindo que o compilador conheça todos os tipos possíveis.

### Exemplo de código

kotlin
sealed class Resultado

class Sucesso(val mensagem: String) : Resultado()

class Erro(val codigo: Int) : Resultado()

object Carregando : Resultado()

fun mostrar(resultado: Resultado) {
when (resultado) {
is Sucesso -> println(resultado.mensagem)
is Erro -> println("Erro: ${resultado.codigo}")
Carregando -> println("Carregando...")
}
}