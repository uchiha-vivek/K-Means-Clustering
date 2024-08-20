package com.kmeans;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

class Point {
    double x, y;
    int cluster;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.cluster = -1;
    }
}

public class KMeansClustering extends JPanel {
    private int numPoints;
    private int numClusters;
    private int maxIterations;
    private ArrayList<Point> points;
    private ArrayList<Point> centroids;

    public KMeansClustering(int numPoints, int numClusters, int maxIterations) {
        this.numPoints = numPoints;
        this.numClusters = numClusters;
        this.maxIterations = maxIterations;
        points = new ArrayList<>();
        centroids = new ArrayList<>();
    }

    private void generateRandomPoints() {
        Random random = new Random();
        points.clear();
        centroids.clear();
        for (int i = 0; i < numPoints; i++) {
            points.add(new Point(random.nextInt(getWidth()), random.nextInt(getHeight())));
        }
        for (int i = 0; i < numClusters; i++) {
            centroids.add(new Point(random.nextInt(getWidth()), random.nextInt(getHeight())));
        }
    }

    private void kMeansClustering() {
        for (int i = 0; i < maxIterations; i++) {
            assignClusters();
            updateCentroids();
        }
        repaint();
    }

    private void assignClusters() {
        for (Point p : points) {
            double minDistance = Double.MAX_VALUE;
            int bestCluster = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = distance(p, centroids.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    bestCluster = i;
                }
            }
            p.cluster = bestCluster;
        }
    }

    private void updateCentroids() {
        double[] sumX = new double[numClusters];
        double[] sumY = new double[numClusters];
        int[] count = new int[numClusters];

        for (Point p : points) {
            sumX[p.cluster] += p.x;
            sumY[p.cluster] += p.y;
            count[p.cluster]++;
        }

        for (int i = 0; i < centroids.size(); i++) {
            centroids.get(i).x = sumX[i] / count[i];
            centroids.get(i).y = sumY[i] / count[i];
        }
    }

    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (points.isEmpty()) {
            generateRandomPoints();
            kMeansClustering();
        }
        for (Point p : points) {
            g.setColor(getColor(p.cluster));
            g.fillOval((int) p.x, (int) p.y, 10, 10);
        }

        g.setColor(Color.BLACK);
        for (Point centroid : centroids) {
            g.fillRect((int) centroid.x - 5, (int) centroid.y - 5, 10, 10);
        }
    }

    private Color getColor(int cluster) {
        switch (cluster) {
            case 0: return Color.RED;
            case 1: return Color.GREEN;
            case 2: return Color.BLUE;
            default: return new Color((cluster * 50) % 255, (cluster * 80) % 255, (cluster * 100) % 255);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("K-Means Clustering");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Control panel
        JPanel controlPanel = new JPanel();
        JLabel pointsLabel = new JLabel("Points:");
        JTextField pointsField = new JTextField("100", 5);
        JLabel clustersLabel = new JLabel("Clusters:");
        JTextField clustersField = new JTextField("3", 5);
        JLabel iterationsLabel = new JLabel("Iterations:");
        JTextField iterationsField = new JTextField("100", 5);
        JButton runButton = new JButton("Run");

        controlPanel.add(pointsLabel);
        controlPanel.add(pointsField);
        controlPanel.add(clustersLabel);
        controlPanel.add(clustersField);
        controlPanel.add(iterationsLabel);
        controlPanel.add(iterationsField);
        controlPanel.add(runButton);

        frame.add(controlPanel, BorderLayout.NORTH);

        // Initial K-Means panel
        KMeansClustering kMeansPanel = new KMeansClustering(
            Integer.parseInt(pointsField.getText()), 
            Integer.parseInt(clustersField.getText()), 
            Integer.parseInt(iterationsField.getText())
        );
        frame.add(kMeansPanel);

        // Button action listener
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int numPoints = Integer.parseInt(pointsField.getText());
                int numClusters = Integer.parseInt(clustersField.getText());
                int maxIterations = Integer.parseInt(iterationsField.getText());

                kMeansPanel.numPoints = numPoints;
                kMeansPanel.numClusters = numClusters;
                kMeansPanel.maxIterations = maxIterations;
                kMeansPanel.points.clear();  
                kMeansPanel.repaint(); 
            }
        });

        frame.setVisible(true);
    }
}
