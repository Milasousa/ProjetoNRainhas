package com.uepb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RainhasAlgoritmoGenetico {

	private int n; //Tamanho do tabuleiro
	private int[] tabuleiro; //Representação do tabuleiro
	private Set<String> posicoesBloqueadas; //Posições bloqueadas

	//Construtor
	public RainhasAlgoritmoGenetico(int n, Set<String> posicoesBloqueadas) {
		this.n = n;
		this.posicoesBloqueadas = posicoesBloqueadas;//Conjunto de posições bloqueadas
		this.tabuleiro = new int[n]; //Tabuleiro
	}
	//Função de aptidão.Neste sentido,vai contar o número de conflitos entre as rainhas
	private int aptidao(int[] tabuleiro) {
		int conflitos = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				if (posicoesBloqueadas.contains(i + "," + tabuleiro[i]) || 
						posicoesBloqueadas.contains(j + "," + tabuleiro[j])) {
					continue; //Tenta ignorar os conflitos,envolvendo posições bloqueadas
				}
				if (Math.abs(tabuleiro[i] - tabuleiro[j]) == Math.abs(i - j)) {
					conflitos++;
				}
			}
		}
		return conflitos;
	}

	//Geração de população inicial com mais diversidade
	private List<int[]> gerarPopulacaoInicial(int tamanhoPopulacao) {
		List<int[]> populacao = new ArrayList<>();
		Random rand = new Random();
		for (int i = 0; i < tamanhoPopulacao; i++) {
			List<Integer> colunas = new ArrayList<>();
			for (int j = 0; j < n; j++) {
				colunas.add(j);
			}
			Collections.shuffle(colunas);

			int[] individuo = new int[n];
			boolean valido = true;
			for (int linha = 0; linha < n; linha++) {
				int coluna = colunas.get(linha);
				if (posicoesBloqueadas.contains(linha + "," + coluna)) {
					valido = false;
					break;
				}
				individuo[linha] = coluna;
			}

			if (valido) {
				populacao.add(individuo);
			} else {
				i--; //Se tiver posição bloqueada refazz
			}
		}
		return populacao;
	}

	//Seleção por torneio com mais diversidade
	private int[] selecaoTorneio(List<int[]> populacao, int tamanhoTorneio) {
		Random rand = new Random();
		List<int[]> torneio = new ArrayList<>();
		for (int i = 0; i < tamanhoTorneio; i++) {
			torneio.add(populacao.get(rand.nextInt(populacao.size())));
		}
		return torneio.stream().min(Comparator.comparingInt(this::aptidao)).get();
	}

	//Cruzamento de múltiplos pontos
	private int[] cruzamento(int[] pai1, int[] pai2) {
		int[] filho = new int[n];
		Random rand = new Random();
		int pontoCruzamento1 = rand.nextInt(n);
		int pontoCruzamento2 = rand.nextInt(n);
		if (pontoCruzamento1 > pontoCruzamento2) {
			int temp = pontoCruzamento1;
			pontoCruzamento1 = pontoCruzamento2;
			pontoCruzamento2 = temp;
		}
		for (int i = 0; i < pontoCruzamento1; i++) {
			filho[i] = pai1[i];
		}
		for (int i = pontoCruzamento1; i < pontoCruzamento2; i++) {
			filho[i] = pai2[i];
		}
		for (int i = pontoCruzamento2; i < n; i++) {
			filho[i] = pai1[i];
		}
		return filho;
	}

	//Mutação.Neste sentido vai trocar duas posições aleatórias,com verificação de bloqueios
	private void mutacao(int[] individuo, double taxaMutacao) {
		Random rand = new Random();
		if (rand.nextDouble() < taxaMutacao) {
			int index1 = rand.nextInt(n);
			int index2 = rand.nextInt(n);

			//Garantir que as posições de mutação não sejam bloqueadas
			while (posicoesBloqueadas.contains(index1 + "," + individuo[index1]) || 
					posicoesBloqueadas.contains(index2 + "," + individuo[index2])) {
				index1 = rand.nextInt(n); //Regenera os índices até serem válidos
				index2 = rand.nextInt(n);
			}
			//Troca as posições
			int temp = individuo[index1];
			individuo[index1] = individuo[index2];
			individuo[index2] = temp;
		}
	}

	//Exibe o tabuleiro em formato de uma matriz,tendo:os bloqueios,rainhas e espaços vazios
	private void exibirTabuleiro(int[] tabuleiro) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (tabuleiro[i] == j) {
					System.out.print("Q "); //Rainha
				} else if (posicoesBloqueadas.contains(i + "," + j)) {
					System.out.print("X "); //Bloqueio
				} else {
					System.out.print(". "); //Espaço vazio
				}
			}
			System.out.println();
		}
	}

	//Resolve o problema usando Algoritmo Genético
	public void executarAlgoritmoGenetico(int tamanhoPopulacao, int geracoes, int tamanhoTorneio, double taxaMutacao) {
		List<int[]> populacao = gerarPopulacaoInicial(tamanhoPopulacao);
		Random rand = new Random();
		int melhorAptidaoAnterior = Integer.MAX_VALUE;
		int geracoesSemMelhora = 0;

		for (int geracao = 0; geracao < geracoes; geracao++) {
			List<int[]> novaPopulacao = new ArrayList<>();
			while (novaPopulacao.size() < tamanhoPopulacao) {
				int[] pai1 = selecaoTorneio(populacao, tamanhoTorneio);
				int[] pai2 = selecaoTorneio(populacao, tamanhoTorneio);
				int[] filho = cruzamento(pai1, pai2);
				mutacao(filho, taxaMutacao);
				novaPopulacao.add(filho);
			}

			populacao = novaPopulacao;

			int[] melhorSolucao = populacao.stream().min(Comparator.comparingInt(this::aptidao)).get();
			int melhorAptidao = aptidao(melhorSolucao);

			if (melhorAptidao == 0) {
				System.out.println("Solução encontrada!");
				melhorAptidaoAnterior = melhorAptidao;
				double mediaAptidao = populacao.stream().mapToInt(this::aptidao).average().orElse(Double.NaN);
				System.out.println("Média da população: " + mediaAptidao);
				System.out.println("Melhor aptidão: " + melhorAptidao);
				System.out.println("Solução encontrada: " + (melhorAptidao == 0 ? "Sim" : "Não"));
				exibirTabuleiro(melhorSolucao);
				return;
			}

			if (melhorAptidao == melhorAptidaoAnterior) {
				geracoesSemMelhora++;
			} else {
				geracoesSemMelhora = 0;
			}

			if (geracoesSemMelhora > 50) {
				System.out.println("Sem melhorias significativas após 50 gerações. Finalizando...");
				return;
			}

			System.out.println("Geração " + geracao + ": Melhor aptidão = " + melhorAptidao);
			exibirTabuleiro(melhorSolucao);
			melhorAptidaoAnterior = melhorAptidao;
			double mediaAptidao = populacao.stream().mapToInt(this::aptidao).average().orElse(Double.NaN);
			System.out.println("Média da população: " + mediaAptidao);
			System.out.println("Melhor aptidão: " + melhorAptidao);
			System.out.println("Solução encontrada: " + (melhorAptidao == 0 ? "Sim" : "Não"));
			System.out.println();
		}

		System.out.println("Solução não encontrada após " + geracoes + " gerações.");
	}

	public static void main(String[] args) {
		int n = 16; //Tamanho do tabuleiro.Caso o n seja alterado,vai precisar alterar o n também no gerador_bloqueios.py, para regenerar o arquivo de bloqueios.text
		double percentualMax = 0.2; //Percentual máximo de bloqueios
		String caminhoArqBloqueios = "C:/Users/User/eclipse-workspace"
				+ "/RainhasComBloqueios/com/uepb/bloqueio/bloqueios.txt"; //Caminho do arquivo gerado pelo gerador_bloqueios.py

		Set<String> posicoesBloqueadas = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new FileReader(caminhoArqBloqueios))) {
			String linha;
			while ((linha = br.readLine()) != null) {
				posicoesBloqueadas.add(linha.trim());
			}
		} catch (IOException e) {
			System.err.println("Erro ao ler bloqueios: " + e.getMessage());
			return;
		}

		long inicio = System.currentTimeMillis(); 
		RainhasAlgoritmoGenetico solver = new RainhasAlgoritmoGenetico(n, posicoesBloqueadas);
		solver.executarAlgoritmoGenetico(100, 1000, 10, 0.15); //Tamanho da população, gerações, torneio, taxa de mutação
		long fim = System.currentTimeMillis();
		System.out.println("Tempo de execução: " + (fim - inicio) + " ms");
	}
}
