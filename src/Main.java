/*
Computer Simulations and Modelling:
-----------------------------------
-----------------------------------
Discrete Event Simulation
--------------------------
ICS 4B
GROUP MEMBERS:
    1. 137766 Myles Johnson
    2. 140801 Kyla Arunga
    3. 137931 Trevor Waweru
*/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

class SimulationFrame extends JFrame {
    private List<Integer> customers;
    private List<Double> interArrivalTimes;
    private List<Double> serviceDurations;
    private List<Double> arrivalTimes;
    private List<Double> serviceStartTimes;
    private List<Double> serviceEndTimes;
    private List<Integer> systemCounts;
    private List<Integer> queueCounts;
    private List<Double> waitTimes;
    private List<Double> timesInSystem;
    private List<Double> idleTimes;

    private int currentSystemCount;
    private int currentQueueCount;

    public SimulationFrame() {
        customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customers.add(i);
        }
        interArrivalTimes = List.of(1.9, 1.3, 1.1, 1.0, 2.2, 2.1, 1.8, 2.8, 2.7, 2.4);
        serviceDurations = List.of(1.7, 1.8, 1.5, 0.9, 0.6, 1.7, 1.1, 1.8, 0.8, 0.5);
        arrivalTimes = new ArrayList<>();
        serviceStartTimes = new ArrayList<>();
        serviceEndTimes = new ArrayList<>();
        systemCounts = new ArrayList<>();
        queueCounts = new ArrayList<>();
        waitTimes = new ArrayList<>();
        timesInSystem = new ArrayList<>();
        idleTimes = new ArrayList<>();

        currentSystemCount = 0;
        currentQueueCount = 0;

        setTitle("Simulation Results");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        runSimulation();
    }

    public void runSimulation() {
        for (int i = 0; i < customers.size(); i++) {
            // Calculate arrival time
            double arrival;
            if (arrivalTimes.isEmpty()) {
                arrival = interArrivalTimes.get(i);
            } else {
                arrival = interArrivalTimes.get(i) + arrivalTimes.get(i - 1);
            }
            arrivalTimes.add(Math.round(arrival * 10.0) / 10.0);

            // Calculate service start time
            double start;
            if (serviceStartTimes.isEmpty()) {
                start = arrivalTimes.get(i);
            } else {
                start = Math.max(arrivalTimes.get(i), serviceEndTimes.get(i - 1));
            }
            serviceStartTimes.add(Math.round(start * 10.0) / 10.0);

            // Calculate service end time
            double end = serviceStartTimes.get(i) + serviceDurations.get(i);
            serviceEndTimes.add(Math.round(end * 10.0) / 10.0);

            // Calculate server idle time
            double idle;
            if (idleTimes.isEmpty()) {
                idle = 0.0;
            } else {
                idle = serviceStartTimes.get(i) - serviceEndTimes.get(i - 1);
            }
            idleTimes.add(Math.round(idle * 10.0) / 10.0);

            // Calculate waiting time in queue
            double wait = serviceStartTimes.get(i) - arrivalTimes.get(i);
            waitTimes.add(Math.round(wait * 10.0) / 10.0);

            // Calculate time in system
            double timeInSys = waitTimes.get(i) + serviceDurations.get(i);
            timesInSystem.add(Math.round(timeInSys * 10.0) / 10.0);

            // Update number in system and number in queue
            if (i == 0) {
                currentSystemCount = 1;
            } else {
                currentSystemCount = 0;
                for (int j = 0; j <= i; j++) {
                    if (arrivalTimes.get(i) < serviceEndTimes.get(j)) {
                        currentSystemCount++;
                    }
                }
            }
            systemCounts.add(currentSystemCount);
            currentQueueCount = currentSystemCount - 1;
            queueCounts.add(currentQueueCount);
        }

        // Display the results in a JTable
        displayResultsInTable();
    }

    private void displayResultsInTable() {
        String[] columnNames = {
                "Customer", "IAT", "Arrival Time", "Service Time", "Service starts", "Service ends", "No. in System", "No. in Queue", "Queue wait Time",
                "Time In System", "Server idle Time"
        };

        Object[][] data = new Object[customers.size() + 1][columnNames.length]; // +1 for the total row
        double totalInterArrivalTime = 0;
        double totalServiceDuration = 0;
        double totalSystemCount = 0;
        double totalQueueCount = 0;
        double totalWaitTime = 0;
        double totalTimeInSystem = 0;
        double totalIdleTime = 0;
        double averageWaitTime = 0;
        double waitProbability = 0;
        double idleTimeProportion = 0;
        double busyTimeProportion = 0;
        double averageServiceDuration = 0;
        double avgWaitTimeForThoseWhoWait = 0;
        double avgTimeInSystem = 0;
        double avgTimeBetweenArrivals = 0;

        for (int i = 0; i < customers.size(); i++) {
            data[i][0] = customers.get(i);
            data[i][1] = interArrivalTimes.get(i);
            data[i][2] = arrivalTimes.get(i);
            data[i][3] = serviceDurations.get(i);
            data[i][4] = serviceStartTimes.get(i);
            data[i][5] = serviceEndTimes.get(i);
            data[i][6] = systemCounts.get(i);
            data[i][7] = queueCounts.get(i);
            data[i][8] = waitTimes.get(i);
            data[i][9] = timesInSystem.get(i);
            data[i][10] = idleTimes.get(i);

            totalInterArrivalTime += interArrivalTimes.get(i);
            totalServiceDuration += serviceDurations.get(i);
            totalSystemCount += systemCounts.get(i);
            totalQueueCount += queueCounts.get(i);
            totalWaitTime += waitTimes.get(i);
            totalTimeInSystem += timesInSystem.get(i);
            totalIdleTime += idleTimes.get(i);
        }

        // Add totals to the last row
        data[customers.size()][0] = "Total";
        data[customers.size()][1] = totalInterArrivalTime; // No total for IAT
        data[customers.size()][2] = ""; // No total for Arrival Time
        data[customers.size()][3] = totalServiceDuration;
        data[customers.size()][4] = ""; // No total for Start Time
        data[customers.size()][5] = ""; // No total for End Time
        data[customers.size()][6] = totalSystemCount;
        data[customers.size()][7] = totalQueueCount;
        data[customers.size()][8] = totalWaitTime;
        data[customers.size()][9] = totalTimeInSystem;
        data[customers.size()][10] = totalIdleTime;

        averageWaitTime = totalWaitTime / customers.size();
        waitProbability = totalQueueCount / (customers.size());
        idleTimeProportion = totalIdleTime / arrivalTimes.get(customers.size() - 1);
        busyTimeProportion = 1 - idleTimeProportion;
        averageServiceDuration = totalServiceDuration / customers.size();
        avgWaitTimeForThoseWhoWait = totalWaitTime / totalQueueCount;
        avgTimeInSystem = totalTimeInSystem / customers.size();
        avgTimeBetweenArrivals = totalInterArrivalTime / (customers.size() - 1);

        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        //Queue Statistics Calculation:
        DecimalFormat formatter = new DecimalFormat("#.##");
        JTextArea metrics = new JTextArea();
        metrics.append("Queue Statistics:\n\n");
        metrics.append("Average Waiting Time: " + formatter.format(averageWaitTime) + "\n");
        metrics.append("Probability that the customer has to wait in the queue: " + formatter.format(waitProbability) + "\n");
        metrics.append("Proportion of idle time of the server: " + formatter.format(idleTimeProportion) + "\n");
        metrics.append("Proportion of time the server was busy: " + formatter.format(busyTimeProportion) + "\n");
        metrics.append("Average service time: " + formatter.format(averageServiceDuration) + "\n");
        metrics.append("Average waiting time for those who have to wait: " + formatter.format(avgWaitTimeForThoseWhoWait) + "\n");
        metrics.append("Average time spent in the system: " + formatter.format(avgTimeInSystem) + "\n");
        metrics.append("Average time between arrivals: " + formatter.format(avgTimeBetweenArrivals) + "\n");

        getContentPane().add(metrics, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationFrame frame = new SimulationFrame();
            frame.setVisible(true);
        });
    }
}
