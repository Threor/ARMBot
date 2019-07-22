package de.arm.bot.dca;

import de.arm.bot.model.Cell;
import de.arm.bot.model.Maze;
import de.arm.bot.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCARunnable implements Runnable {

    private Maze maze;

    public DCARunnable(Maze maze) {
        this.maze = maze;
    }

    @Override
    public void run() {
        List<Cell> discoveredCells = maze.getAllDiscoveredCells();
        Map<DCAStatus, List<Cell>> cellsToDCAStatus = discoveredCells.stream()
                .collect(Collectors.toMap(Cell::getDCAStatus, c -> new ArrayList<>(), (a, b) -> {
                            a.addAll(b);
                            return (ArrayList<Cell>) a;
                        },
                        HashMap::new));
        cellsToDCAStatus.get(DCAStatus.C0).forEach(c -> c.setStatus(Status.DEAD));
        cellsToDCAStatus.get(DCAStatus.C1).forEach(c -> c.setStatus(Status.DEAD));
        cellsToDCAStatus.get(DCAStatus.C2).forEach(this::c2Routine);
        cellsToDCAStatus.get(DCAStatus.C3).forEach(this::c3Routine);
        cellsToDCAStatus.get(DCAStatus.C4).forEach(this::c4Routine);
    }

    private void c2Routine(Cell c) {

    }

    private void c3Routine(Cell c) {

    }

    private void c4Routine(Cell c) {

    }

}
