public class ExceptionsGraph  extends Exception {
    public void LessLedges(int k, int n) throws Exception {
        if (k <= 0) {
            throw new IllegalArgumentException("El número de aristas no puede ser negativo.");
        }
        if (n < k){
            throw new IllegalArgumentException("El número de vértices no puede ser menor que el número de aristas.");
        }
        
    }
}
