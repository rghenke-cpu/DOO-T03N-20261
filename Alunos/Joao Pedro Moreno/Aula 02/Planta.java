import java.util.Scanner;

public class Planta {

    static Scanner scan = new Scanner(System.in);

    int quant;
    double valor;

    public Planta() {
    }

    public int leituraQuant() {
        System.out.println("\nInforme a quantidade da planta:");
        quant = scan.nextInt();
        scan.nextLine();
        return quant;
    }

    public double leituraValor() {
        System.out.println("Informe o valor da planta:");
        valor = scan.nextDouble();
        scan.nextLine();
        return valor;
    }

    public double calcularPrecoTotal() {
        return quant * valor;
    }

}