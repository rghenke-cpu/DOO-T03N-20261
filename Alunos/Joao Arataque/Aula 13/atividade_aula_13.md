# Atividade Extra - Aula 13

## Nome:
João Gabriel Gnoatto

---

## Conceito escolhido 1: Coroutines

**Timestamp do vídeo que menciona o conceito:** aproximadamente **2:55**  

### O que é?

Coroutines são um recurso do Kotlin usado para trabalhar com tarefas assíncronas de forma mais simples e leve. Elas permitem executar operações que podem demorar, como acessar uma API, buscar dados em um banco ou esperar uma resposta de rede, sem travar a execução principal do programa.

Diferente de criar várias threads manualmente, as coroutines são mais leves e podem ser suspensas e retomadas depois. Isso significa que o programa consegue “pausar” uma tarefa enquanto espera uma resposta e continuar executando outras coisas.

### Para que serve?

Elas servem principalmente para melhorar a performance e a organização de códigos que trabalham com tarefas demoradas. Em aplicativos Android, por exemplo, são muito usadas para buscar dados da internet sem congelar a tela do usuário.

### Como é normalmente utilizado?

Normalmente, as coroutines são usadas com funções como `runBlocking`, `launch`, `async` e funções marcadas com `suspend`. A palavra-chave `suspend` indica que aquela função pode ser pausada sem bloquear a thread.

### Exemplo de código

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        buscarDados()
    }

    println("Enquanto isso, o programa continua executando...")
}

suspend fun buscarDados() {
    delay(2000)
    println("Dados carregados com sucesso!")
}
```

Nesse exemplo, a função `buscarDados()` simula uma tarefa demorada usando `delay`. A coroutine permite esperar essa tarefa sem bloquear totalmente o funcionamento do programa.

---

## Conceito escolhido 2: Extension Functions

**Timestamp do vídeo que menciona o conceito:** aproximadamente **3:10**

### O que é?

Extension Functions, ou funções de extensão, são funções que permitem adicionar novos comportamentos a uma classe sem precisar alterar diretamente o código original dela.

Isso é útil quando queremos melhorar ou personalizar o uso de uma classe existente, como `String`, `Int`, `List` ou até classes criadas por outras bibliotecas.

### Para que serve?

Elas servem para deixar o código mais organizado, legível e reutilizável. Em vez de criar uma função separada e passar o objeto como parâmetro, podemos chamar a função como se ela pertencesse ao próprio objeto.

### Como é normalmente utilizado?

Para criar uma função de extensão, escrevemos o tipo que queremos estender, seguido de um ponto e do nome da nova função. Dentro da função, usamos `this` para representar o objeto que está chamando a função.

### Exemplo de código

```kotlin
fun String.primeiraLetraMaiuscula(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun main() {
    val nome = "joão"
    println(nome.primeiraLetraMaiuscula())
}
```

Nesse exemplo, foi criada uma função de extensão para a classe `String`. Assim, qualquer texto pode chamar `primeiraLetraMaiuscula()` como se essa função já existisse originalmente dentro da classe `String`.
