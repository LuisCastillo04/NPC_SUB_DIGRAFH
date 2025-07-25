### Contenido de cada notebook

1. **Notebook_Graficas.ipynb**
  
  - Leer los CSV de resultados (Python y Java).
    
  - Convertir `"TIMEOUT"` → `NaN`.
    
  - Crear DataFrames numéricos listos para gráficos.
    
2. **Creación de gráficos**
  
  - Gráficos de tiempo de ejecución:
    
    - Escala lineal
      
    - Escala logarítmica (`log`)
      
    - Escala simétrica-logarítmica (`symlog`)
      
  - Marcadores “X” para timeouts.
    
  - Comparativa Python vs Java con paletas de color.
    
3. **Visualización de Operaciones**
  
  - Conteo de operaciones (BF, DC, BT) para cada implementación.
    
  - Gráficas en escala lineal y log.
    
  - Señalización de timeouts.
    
4. **ydata_profiling**
  
  - Generación de un `ProfileReport` con **ydata-profiling** para cada algoritmo.
    
  - Matrices de correlación (Pearson/Spearman).
    
  - Exportación de informe HTML.
    

---

### Instrucciones

Para visualizar los informes HTML generados, una de las opciones es:

- **Doble clic en el explorador de archivos**
  
  1. Abre tu gestor de archivos (Explorador en Windows, Finder en macOS o tu explorador en Linux).
    
  2. Navega hasta la carpeta donde están los `Report_*.html`.
    
  3. Haz doble clic sobre el informe deseado; se abrirá en tu navegador web predeterminado.