    import java.util.Scanner;

public class Main {

	public static Scanner scan = new Scanner(System.in);
	static double totalCompra;

	public static void main(String[] args) {
		
		VerMenu();
	}

	public static void VerMenu() {
		int escolha = 0;

		do {
			System.out.println("\n\n___________________________________");
			System.out.println("|--------------Menu---------------|");
			System.out.println("|1- Calcular Preço Total          |");
			System.out.println("|2- Calcular troco                |");
			System.out.println("|3- Sair                          |");
			System.out.println("|Escolha uma opção:               |");
			System.out.printf("|_________________________________|\n");
			escolha = scan.nextInt();
			scan.nextLine();
			validarEscolha(escolha);
		} while (escolha != 3);
	}

	public static void validarEscolha(int escolha) {
		switch (escolha) {
		case 1: {
			CalcularPrecoTotal();
			break;
		}
		case 2: {
			CalcularTroco();
			break;
		}
		case 3: {
			System.out.printf("Você saiu do sistema! Obrigado, volte sempre!");
			return;
		}
		default:
			System.out.printf("Opção incorreta, tente novamente!");
		}
	}

	public static void CalcularPrecoTotal() {

		Planta p = new Planta();

		p.leituraQuant();
    	p.leituraValor();

    totalCompra = p.calcularPrecoTotal();

    System.out.println("O valor total das plantas: " + totalCompra);

	}

	public static void CalcularTroco() {

     System.out.println("\nInforme o valor pago pelo cliente:");
    double valorPago = scan.nextDouble();

    if (valorPago < totalCompra) {
        System.out.println("Valor insuficiente! O cliente deve pagar pelo menos: " + totalCompra + " reais");
    } else {

        double troco = valorPago - totalCompra;

        System.out.println("Total da compra: " + totalCompra);
        System.out.println("Troco do cliente: " + troco);
    }
}
}
