# NPC_SUB_DIGRAFH
Problema NP-completo

**Autores:** 
- Sebastian Rodriguez Giraldo - srodriguezgi
- Luis Eduardo Miño Castillo - lmino

## Fuente:
>- **Number:** 5
>- **Name:** Bipartite Subgraph [GT25] 2
>- **Input:** An n-node undirected graph G(V,E) with node set V and edge set E; a positive integer k with k<=|E|.
>- **Question:** Is there a subset, F of the edges of G, having size at least k and such that the graph H(V,F) is bipartite?- >**Comments:** G(V,E) is bipartite if the nodes can be partitioned into two disjoint sets U and W such that every edge of G connects a node in U to a node in W, i.e. no two nodes in U (resp. W) form an edge of G.

Figura como Bipartite Subgraph [GT25] 2 en la [lista anotada de problemas NP-completos](https://www.csc.liv.ac.uk/~ped/teachadmin/COMP202/annotated_np.html).



## Instrucciones de Uso

Este repositorio contiene dos implementaciones del problema **Bipartite Subgraph**:

- **Python** en `NPC_SUB_DIGRAFH_PYTHON_CODE`  
- **Java** en `NPC_SUB_DIGRAFH_JAVA_CODE`

---

## 📦 Requisitos (Windows)

### Java
- **JDK 22**  
  1. Descarga el instalador Windows x64 desde  
     https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html  
  2. Ejecuta el `.exe`, marca **Add to PATH** y finaliza la instalación.  
  3. Verifica en PowerShell:  
     ```powershell
     java -version
     ```
- **IDE: NetBeans**  
  1. Descarga el instalador desde https://netbeans.apache.org/download/index.html  
  2. Ejecuta el `.exe` y selecciona el JDK 22 durante la instalación.

- **Dependencias Java** (en `NPC_SUB_DIGRAFH_JAVA_CODE/lib/`):  
  - `jgrapht-core.jar`  
  - `commons-math3.jar`  

---

### Python
- **Python 3.13.5**  
  1. Descarga el instalador Windows x64 en  
     [https://www.python.org/downloads/release/python-135/](https://www.python.org/downloads/)  
  2. Ejecuta el `.exe`, marca **Add Python to PATH** y finaliza la instalación.  
  3. Verifica en PowerShell:  
     ```powershell
     python --version
     ```
- **IDE: Visual Studio Code**  
  1. Descarga el instalador desde https://code.visualstudio.com/download  
  2. Instala y, desde el Marketplace, agrega la extensión **Python** (Microsoft).  




