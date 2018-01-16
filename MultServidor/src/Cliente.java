import java.io.*;
import java.net.*;
import java.util.*;


public class Cliente {

	public static void menu(){
		System.out.println("DIGITE QUAL CONTA DESEJA FAZER: ");
		System.out.println("1- SOMA");
		System.out.println("2- SUBTRACAO");
		System.out.println("3- DIVISAO");
		System.out.println("4- MULTIPLICACAO");
		System.out.println("5- SAIR");
		
	}

	public static void main(String[] args) {
		
		//INICIA AS VARIAVEIS
		Scanner scanner = new Scanner(System.in);
		ObjectOutputStream enviaMensagem;
		ObjectInputStream recebeMensagem;
		String calc;
		Socket socketCliente = null;
		try {
			System.out.println("Digite o ip do servidor principal: ");
			String ipServidor = scanner.nextLine();
			socketCliente = 	new Socket(ipServidor, 8888); //CONECTA COM O SERVIDOR PRINCIPAL PELA SOCKET 8888
			System.out.println("CLIENTE CONECTADO AO SERVIDOR PRINCIPAL NO IP " + ipServidor);
			
			//INICIA VARIAVEIS DE ENTRADA E SAIDA DOS DADOS
			recebeMensagem = new ObjectInputStream(socketCliente.getInputStream());
			enviaMensagem = new ObjectOutputStream(socketCliente.getOutputStream());
			
			while (socketCliente.isConnected()){
				menu();
				int opc = scanner.nextInt();
				if(opc == 5){
					try {
						enviaMensagem.writeObject(String.valueOf(opc));
						socketCliente.close();
						System.exit(-1);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				float n1, n2;
				System.out.println("DIGITE O PRIMEIRO OPERANDO");
				n1 = scanner.nextFloat();
				System.out.println("DIGITE O SEGUNDO OPERANDO");
				n2 = scanner.nextFloat();
				
				calc = opc + "#" + n1 + "#" + n2 + "#"; //CONCATENA OS NUMEROS EM UMA STRING PARA ENVIAR PARA O SERVIDOR

				//ENVIA PARA SERVIDOR O NUMERO DIGITADO PELO USUARIO
				System.out.println("Enviando para o SERVIDOR " + calc + "\n");
				enviaMensagem.writeObject(calc);

				System.out.println("RESULTADO DA OPECARACAO: " + recebeMensagem.readObject().toString());


			}


		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}


