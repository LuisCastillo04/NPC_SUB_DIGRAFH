# Subgrafos bipartitos: análisis de un problema NP-completo

Proyecto académico sobre **Bipartite Subgraph**, con implementaciones en **Python y Java** y comparación experimental de estrategias de búsqueda.

## Problema

Dado un grafo no dirigido `G = (V, E)` y un entero `k`, se busca determinar si existe un subconjunto `F ⊆ E` con al menos `k` aristas tal que `H = (V, F)` sea bipartito. Un grafo es bipartito cuando sus vértices pueden dividirse en dos conjuntos y cada arista conecta vértices de conjuntos distintos.

La referencia del trabajo es **Bipartite Subgraph [GT25]**, citada en la [lista anotada de problemas NP-completos](https://www.csc.liv.ac.uk/~ped/teachadmin/COMP202/annotated_np.html).

## Qué contiene

- Implementaciones en Python y Java.
- En Python: fuerza bruta, divide y vencerás y backtracking.
- Generación de grafos aleatorios con semillas reproducibles.
- Medición de tiempos y conteo instrumentado de operaciones.
- Resultados en CSV e informes del trabajo.

## Estructura

| Carpeta | Contenido |
| --- | --- |
| [NPC_SUB_DIGRAFH_PYTHON_CODE](NPC_SUB_DIGRAFH_PYTHON_CODE/) | Algoritmos, programa experimental, temporizador, pruebas y resultados CSV. |
| [NPC_SUB_DIGRAFH_JAVA_CODE](NPC_SUB_DIGRAFH_JAVA_CODE/) | Implementación Java y dependencias del proyecto. |
| [INFORME_Y_NOTEBOOK](INFORME_Y_NOTEBOOK/) | Material de análisis e informe académico. |

## Ejecutar el experimento en Python

Desde una terminal, con Python instalado:

```bash
git clone https://github.com/LuisCastillo04/NPC_SUB_DIGRAFH.git
cd NPC_SUB_DIGRAFH/NPC_SUB_DIGRAFH_PYTHON_CODE
python -m pip install networkx pandas
python main.py
```

La versión de Python documentada en el trabajo original es **3.13.5**. `Algoritmos.py` utiliza NetworkX y `main.py` también importa pandas.

`main.py` genera el archivo `results.csv` en el directorio de ejecución y **sobrescribe ese archivo si ya existe**. Guarda una copia antes de ejecutar si deseas conservar una medición anterior.

Los parámetros del experimento están al comienzo de `main.py`:

| Parámetro | Valor actual | Propósito |
| --- | --- | --- |
| `SEED_BASE` | `777` | Semilla base para generar instancias reproducibles. |
| `N_TESTS` | `20` | Número de instancias generadas. |
| `REPS` | `5` | Repeticiones por algoritmo. |
| `TIMEOUT` | `15` segundos | Límite por llamada a un algoritmo. |

El CSV registra la instancia, los tiempos y los contadores de cada estrategia. `TIMEOUT` indica que una llamada alcanzó el límite configurado; no equivale a una respuesta negativa al problema.

## Implementación Java

El entorno documentado originalmente utiliza **JDK 22** y **Apache NetBeans**. El proyecto se encuentra en `NPC_SUB_DIGRAFH_JAVA_CODE/` y referencia las bibliotecas `jgrapht-core.jar` y `commons-math3.jar` en su carpeta `lib/`.

## Interpretación de resultados

Este trabajo tiene fines académicos. Los tiempos dependen del tamaño y la estructura de los grafos, la máquina y el entorno de ejecución. Los contadores reflejan la instrumentación del código y no representan directamente instrucciones del procesador.

Equipo de referencia del trabajo original: Intel Core i5-10300H, 16 GB de RAM y Windows 11 de 64 bits.

## Autores

- Sebastian Rodriguez Giraldo — `srodriguezgi`
- Luis Eduardo Miño Castillo — `lmino`
