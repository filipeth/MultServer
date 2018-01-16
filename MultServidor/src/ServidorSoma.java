import java.io.*;
import java.net.*;
import java.util.Scanner;


public class ServidorSoma {

	public static void main(String[] args) {
		
		//DECLARACAO DE VARIAVEIS
		ObjectOutputStream enviaMensagem;
		ObjectInputStream recebeMensagem;
		Scanner scanner = new Scanner(System.in);
		Socket socketCliente = null;
		String calc;
		float valores[] = new float[2];
		
		try {
			
			//INICIA CONEXAO COM O SERVIDOR PRINCIPAL
			System.out.println("Digite o ip do servidor principal: ");
			String ipServidor = scanner.nextLine();
			socketCliente = 	new Socket(ipServidor, 9999);
			System.out.println("SERVIDOR DE SOMA CONECTADO AO SERVIDOR PRINCIPAL DE IP: " + ipServidor);
			
			//INICIA VARIAVEIS DE ENTRADA E SAIDA DOS DADOS
			recebeMensagem = new ObjectInputStream(socketCliente.getInputStream());
			enviaMensagem = new ObjectOutputStream(socketCliente.getOutputStream());
			
			
			while (socketCliente.isConnected()){ //ENQUANTO CLIENTE ESTA CONECTADO
				calc = (String) recebeMensagem.readObject(); //METODO QUE LE O SOCKET CONTENDO OS VALORES PARA REALIZAR O CALCULO
				System.out.println("SERVIDOR ENVIOU: " + calc);
				String str[] = calc.split("#"); //COMO OS NUMEROS VEM EM UMA MENSAGEM ESSE METODO SEPARA OS NUMEROS PARA SEREM CALCULADOS
				valores[0] = Float.valueOf(str[0]);
				valores[1] = Float.valueOf(str[1]);
				float resultado = valores[0]+valores[1];
				System.out.println("ENVIANDO AO SERVIDOR: " + resultado);
				enviaMensagem.writeObject(resultado); //ENVIA PARA O SERVIDOR O RESULTADO DA OPERACAO
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				socketCliente.close(); //FECHA O SOCKET QUANDO O PROGRAMA FOR ENCERRADO
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
