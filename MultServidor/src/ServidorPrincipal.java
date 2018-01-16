import java.io.*;
import java.net.*;
import java.util.*;




public class ServidorPrincipal implements Runnable{
	
	private Socket csocket;
	private ObjectOutputStream enviaMensagemCliente, enviaMensagemSoma, enviaMensagemSub, enviaMensagemDiv, enviaMensagemMult;
	private ObjectInputStream recebeMensagemCliente, recebeMensagemSoma, recebeMensagemSub, recebeMensagemDiv, recebeMensagemMult;
	private ArrayList<String> listaClientes;
	
	
	//METODO CONSTRUTOR DA CLASSE
	public ServidorPrincipal(ArrayList<String> listaClientes, ObjectOutputStream enviaMensagemSoma,
			ObjectOutputStream enviaMensagemSub,
			ObjectOutputStream enviaMensagemDiv,
			ObjectOutputStream enviaMensagemMult,
			ObjectInputStream recebeMensagemSoma,
			ObjectInputStream recebeMensagemSub,
			ObjectInputStream recebeMensagemDiv,
			ObjectInputStream recebeMensagemMult, Socket csocket) throws IOException {
		this.csocket = csocket;
		this.enviaMensagemSoma = enviaMensagemSoma;
		this.enviaMensagemSub = enviaMensagemSub;
		this.enviaMensagemDiv = enviaMensagemDiv;
		this.enviaMensagemMult = enviaMensagemMult;
		this.recebeMensagemSoma = recebeMensagemSoma;
		this.recebeMensagemSub = recebeMensagemSub;
		this.recebeMensagemDiv = recebeMensagemDiv;
		this.recebeMensagemMult = recebeMensagemMult;
		this.enviaMensagemCliente = new ObjectOutputStream(csocket.getOutputStream());
		this.recebeMensagemCliente = new ObjectInputStream(csocket.getInputStream());
		this.listaClientes = listaClientes;
	}
	
	public static void main(String[] args) {
		
		ServerSocket sserver = null;
		ServerSocket sclient = null;
		ArrayList<String> listaClientes = new ArrayList<String>();
		Socket servSoma, servSub = null, servDiv = null, servMult = null;

		try {
			sserver = new ServerSocket(9999); //INICIA SERVIDOR PRINCIPAL NO SOCKET 9999 PARA CONECAO COM SERVIDORES DE CALCULO
			System.out.println("INICIANDO CONECAO COM OS SERVIDORES DA CALCULADORA ");
							
			//INICIA CONECAO E FLUXO DE DADOS COM OS SERVIDORES DE OPERACOES
			System.out.println("AGUARDANDO CONEXAO COM O SERVIDOR SOMA");
			servSoma = sserver.accept();
			ObjectOutputStream enviaMensagemSoma = new ObjectOutputStream(servSoma.getOutputStream());
			ObjectInputStream recebeMensagemSoma = new ObjectInputStream(servSoma.getInputStream());
			System.out.println("Servidor para calculo de soma no IP: " + servSoma.getInetAddress().toString().substring(1));
			
			System.out.println("AGUARDANDO CONEXAO COM O SERVIDOR SUBTRACAO");
			servSub = sserver.accept();
			ObjectOutputStream enviaMensagemSub = new ObjectOutputStream(servSub.getOutputStream());
			ObjectInputStream recebeMensagemSub = new ObjectInputStream(servSub.getInputStream());
			System.out.println("Servidor para calculo de subtracao no IP: " + servSub.getInetAddress().toString().substring(1));
			
			System.out.println("AGUARDANDO CONEXAO COM O SERVIDOR DIVISAO");
			servDiv = sserver.accept();
			ObjectOutputStream enviaMensagemDiv = new ObjectOutputStream(servDiv.getOutputStream());
			ObjectInputStream recebeMensagemDiv = new ObjectInputStream(servDiv.getInputStream());
			System.out.println("Servidor para calculo de divisao no IP: " + servDiv.getInetAddress().toString().substring(1));
			
			System.out.println("AGUARDANDO CONEXAO COM O SERVIDOR MULTIPLICACAO");
			servMult = sserver.accept();
			ObjectOutputStream enviaMensagemMult = new ObjectOutputStream(servMult.getOutputStream());
			ObjectInputStream recebeMensagemMult = new ObjectInputStream(servMult.getInputStream());
			System.out.println("Servidor para calculo de multiplicacao no IP: " + servMult.getInetAddress().toString().substring(1));
			
			
			sclient = new ServerSocket(8888); //INICIA SERVIDOR PRINCIPAL NO SOCKET 8888 PARA CONECAO COM CLIENTES
			System.out.println("\n------ONLINE------");
			
			//ENQUANTO ESTIVER ONLINE, ESPERA CONEXAO COM CLIENTE
			while (true) {
				System.out.println("ESPERANDO CONECAO COM CLIENTE");
				Socket sock = sclient.accept(); //METODO PARA ACEITAR CONEXAO COM O CLIENTE
				System.out.println("CLIENTE " + sock.getInetAddress().toString().substring(1) + " CONECTOU." );
				//CRIA UMA NOVA TRHREAD PARA TRATAR O NOVO CLIENTE CONECTADO
				//AS VARIAVEIS DE FLUXO DE DADOS SAO PASSADOS POR PARAMENTROS PARA QUE TODAS THREADS COMPARTILHEM O MESMO CANAL DE COMUNICACAO
				new Thread(new ServidorPrincipal(listaClientes, enviaMensagemSoma, enviaMensagemSub, enviaMensagemDiv, 
						enviaMensagemMult, recebeMensagemSoma, recebeMensagemSub, recebeMensagemDiv, recebeMensagemMult, sock)).start();
				
			}
			
		} catch (IOException e) {
			System.out.println("Nao foi possivel criar o servidor na porta desejada. Saindo. ");
			e.printStackTrace();
			System.exit(-1);
		} finally {
			try {
				sserver.close();
				sclient.close();
			} catch (IOException e) {
				System.out.println("Erro ao fechar o socket!");
				e.printStackTrace();
			}
		}



	}
	
	
	//METODO QUE SERA EXECUTADO QUANDO UM NOVO CLIENTE CONECTAR
	public void run() {
		
		String ipcliente = csocket.getInetAddress().toString().substring(1); //PEGA O ENDERECO DO CLIENTE CONECTADO
		listaClientes.add(ipcliente ); //ADICIONA O CLIENTE A LISTA DE CLIENTES CONECTADOS
		boolean conectado = true;
		System.out.println("CLIENTES CONECTADOS: " + listaClientes.toString());
		//ENQUANTO O CLIENTE ESTIVER CONECTADO
		while(conectado){
			try {
				String recebido = (String) recebeMensagemCliente.readObject(); //METODO QUE RECEBE OS NUMEOROS DO CLIENTE
				String val[] = recebido.split("#"); //COMO OS NUMEROS VEM EM UMA MENSAGEM ESSE METODO SEPARA OS NUMEROS PARA SEREM REDIRECIONADOS
				int opc = Integer.valueOf(val[0]);
				Float resultado = null;
				switch (opc) {
				case 1:
					System.out.println("ENVIADO PARA O SERVIDOR SOMA: " + val[1] + " + " +val[2]);
					enviaMensagemSoma.writeObject(val[1] + "#" +val[2]); //MEOTODO QUE ENVIA OS VALORES ENVIADOS PELO CLIENTE PARA O SERVIDOR DA OPERACAO
					resultado = (Float) recebeMensagemSoma.readObject();
					System.out.println("RESULTADO: " + resultado + " ENVIADO PARA O CLIENTE " + ipcliente);
					enviaMensagemCliente.writeObject(resultado);
					break;
				case 2:
					System.out.println("ENVIADO PARA O SERVIDOR SUB: " + val[1] + " - " +val[2]);
					enviaMensagemSub.writeObject(val[1] + "#" +val[2]);//MEOTODO QUE ENVIA OS VALORES ENVIADOS PELO CLIENTE PARA O SERVIDOR DA OPERACAO
					resultado = (Float) recebeMensagemSub.readObject();
					System.out.println("RESULTADO: " + resultado + " ENVIADO PARA O CLIENTE " + ipcliente);
					enviaMensagemCliente.writeObject(resultado);
					break;
				case 3:
					System.out.println("ENVIADO PARA O SERVIDOR DIV: " + val[1] + " / " +val[2]);
					enviaMensagemDiv.writeObject(val[1] + "#" +val[2]);//MEOTODO QUE ENVIA OS VALORES ENVIADOS PELO CLIENTE PARA O SERVIDOR DA OPERACAO
					resultado = (Float) recebeMensagemDiv.readObject();
					System.out.println("RESULTADO: " + resultado + " ENVIADO PARA O CLIENTE " + ipcliente);
					enviaMensagemCliente.writeObject(resultado);
					break;
				case 4:
					System.out.println("ENVIADO PARA O SERVIDOR MULT: " + val[1] + " * " +val[2]);
					enviaMensagemMult.writeObject(val[1] + "#" +val[2]);//MEOTODO QUE ENVIA OS VALORES ENVIADOS PELO CLIENTE PARA O SERVIDOR DA OPERACAO
					resultado = (Float) recebeMensagemMult.readObject();
					System.out.println("RESULTADO: " + resultado + " ENVIADO PARA O CLIENTE " + ipcliente);
					enviaMensagemCliente.writeObject(resultado);
					break;
				case 5:
					listaClientes.remove(ipcliente);
					System.out.println("CLIENTE " + ipcliente +" DESCONECTADO");
					conectado = false;
					csocket.close();
					break;

				}	
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}

		}

	}
	

}
