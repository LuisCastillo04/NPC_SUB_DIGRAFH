/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CodigoJava;


public class Timer {
    private long startTime;
    private double elapsed;

    public void start() {
        this.startTime = System.nanoTime();
    }

    public double stop() {
        long endTime = System.nanoTime();
        this.elapsed = (endTime - startTime) / 1e9;
        return this.elapsed;
    }
}
