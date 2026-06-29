## Conceito 1: Bit Rot

**Timestamp do vídeo:** 01:46

### O que é?

Bit Rot (também chamado de *data degradation*) é um fenômeno em que dados armazenados podem sofrer alterações ou corrupção ao longo do tempo devido a falhas físicas do meio de armazenamento, desgaste do hardware ou erros de leitura e gravação.

### Para que serve?

O conceito é importante para conscientizar sobre a necessidade de manter backups, verificar a integridade dos arquivos e utilizar sistemas de armazenamento confiáveis.

### Como é normalmente utilizado?

Embora o Bit Rot não seja uma técnica utilizada diretamente pelo programador, sistemas de arquivos modernos, bancos de dados e serviços de backup utilizam verificações de integridade (checksums e hashes) para detectar esse tipo de problema.

### Exemplo de código

```java
import java.security.MessageDigest;

public class HashExemplo {
    public static void main(String[] args) throws Exception {
        String texto = "Arquivo importante";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(texto.getBytes());
        System.out.println(java.util.HexFormat.of().formatHex(hash));
    }
}
```

Nesse exemplo, um hash pode ser utilizado para verificar se o conteúdo foi alterado ao longo do tempo.

---

## Conceito 2: Magic Numbers

**Timestamp do vídeo:** 02:37

### O que é?

Magic Numbers são valores numéricos inseridos diretamente no código sem uma explicação clara sobre seu significado.

### Para que serve?

Na verdade, eles não são recomendados. O ideal é substituí-los por constantes com nomes descritivos, tornando o código mais legível e fácil de manter.

### Como é normalmente utilizado?

Em projetos bem estruturados, valores importantes são declarados como constantes para facilitar futuras alterações e melhorar a compreensão do código.

### Exemplo de código

**Forma não recomendada:**

```java
double total = valor * 1.15;
```

**Forma recomendada:**

```java
public class Exemplo {
    static final double TAXA = 1.15;

    public static void main(String[] args) {
        double valor = 100;
        double total = valor * TAXA;
        System.out.println(total);
    }
}
```

Nesse caso, a constante `TAXA` deixa claro o significado do valor `1.15`, tornando o código mais organizado e de fácil manutenção.

---

*Centro Universitário FAG — Engenharia de Software, 3º Período*
