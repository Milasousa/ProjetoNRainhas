import random

def gerar_tabuleiro_com_bloqueios_melhorado(n, num_bloqueios=None, seed=None, percentual_max=0.2):
    """
    Gera um tabuleiro n x n com bloqueios em posições aleatórias.

    Parâmetros:
        n (int): tamanho do tabuleiro (ex: 8, 11, 32, 109, 128 até 512)
        num_bloqueios (int, opcional): número de casas bloqueadas. Se None, calcula como 7% de N^2.
        seed (int, opcional): semente para geração aleatória.
        percentual_max (float): percentual máximo de bloqueios permitido (0.0 a 1.0).

    Retorna:
        tabuleiro (list de listas): matriz n x n com '.', 'X' para bloqueios
        bloqueios (list de tuplas): posições bloqueadas
    """
    
    if seed is not None:
        random.seed(seed)

    total_casas = n * n
    max_bloqueios = int(min(total_casas - n, percentual_max * total_casas))  # ainda precisa sobrar pelo menos n posições

    if num_bloqueios is None:
        num_bloqueios = int(0.07 * total_casas)  # coloquei o mínimo: 7%; máx: 13%
    elif num_bloqueios > max_bloqueios:
        raise ValueError(f"Número de bloqueios ({num_bloqueios}) excede o máximo permitido ({max_bloqueios}) para n={n}.")

    tabuleiro = [['.' for _ in range(n)] for _ in range(n)]
    bloqueios = set()

    while len(bloqueios) < num_bloqueios:
        i, j = random.randint(0, n - 1), random.randint(0, n - 1)
        if (i, j) not in bloqueios:
            tabuleiro[i][j] = 'X'
            bloqueios.add((i, j))

    return tabuleiro, list(bloqueios)
def salvar_bloqueios_em_arquivo(bloqueios, arquivo="bloqueios.txt"):
    with open(arquivo, "w") as f:
        for i, j in bloqueios:
            f.write(f"{i},{j}\n")
def imprimir_tabuleiro(tabuleiro, limite=32):
    """
    Imprime o tabuleiro de forma legível (limita visualização para n muito grande).
    """
    n = len(tabuleiro)
    for i in range(min(n, limite)):
        linha = " ".join(tabuleiro[i][:limite])
        print(linha)

# Exemplo de uso
if __name__ == "__main__":
    #Vai precisar alterar o n também no RainhasAlgoritmoGenetico.java
    n = 16
    tabuleiro, bloqueios = gerar_tabuleiro_com_bloqueios_melhorado(n=n, seed=10)
    salvar_bloqueios_em_arquivo(bloqueios)
    imprimir_tabuleiro(tabuleiro)
    print(f"\nBloqueios: {bloqueios}")