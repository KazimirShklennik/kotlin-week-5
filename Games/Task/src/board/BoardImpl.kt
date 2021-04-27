package board

import board.Direction.*

open class BoardImpl(override val width: Int) : SquareBoard {
    var cells = mutableListOf<Cell>()
    var boardArray = Array(width) { Array(width) {} }

    init {
        for (i in 0..boardArray.size - 1) {
            for (j in 0..boardArray.size - 1) {
                cells.add(Cell(i + 1, j + 1))
            }
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return cells
                .stream()
                .filter { it.i == i && it.j == j }
                .findFirst()
                .orElse(null)
    }

    override fun getCell(i: Int, j: Int): Cell {
        var cell = cells
                .stream()
                .filter { it.i == i && it.j == j }
                .findFirst()
                .orElse(null)

        if (cell == null) {
            throw IllegalArgumentException()
        }
        return cell
    }

    override fun getAllCells(): Collection<Cell> {
        return cells
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        var rows = mutableListOf<Cell>()
        for (cell in cells) {
            if (cell.i == i && cell.j in jRange) {
                rows.add(cell)
            }
        }

        var last = jRange.lastOrNull()

        if (last != null && jRange.first > last) {
            return rows.reversed()
        }

        return rows
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {

        var columns = mutableListOf<Cell>()
        for (cell in cells) {
            if (cell.j == j && cell.i in iRange) {
                columns.add(cell)
            }
        }

        var last = iRange.lastOrNull()

        if (last != null && iRange.first > last) {
            return columns.reversed()
        }

        return columns
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        var result: Cell?
        when (direction) {
            UP ->
                result = cells.stream()
                        .filter { it.i == i - 1 && it.j == j }
                        .findFirst()
                        .orElse(null)

            DOWN -> result = cells.stream()
                    .filter { it.i == i + 1 && it.j == j }
                    .findFirst()
                    .orElse(null)

            LEFT -> result = cells.stream()
                    .filter { it.i == i && it.j == j - 1 }
                    .findFirst()
                    .orElse(null)

            RIGHT -> result = cells.stream()
                    .filter { it.i == i && it.j == j + 1 }
                    .findFirst()
                    .orElse(null)
        }
        return result
    }
}

inline fun <T> mutableList(size: Int = 0, init: (index: Int) -> T): MutableList<T> {
    val list = ArrayList<T>(size)
    repeat(size) { index -> list.add(init(index)) }
    return list
}

class GameBoardImpl<T>(width: Int) : GameBoard<T>, BoardImpl(width) {

    private var gameBoardArray = mutableListOf<MutableList<T?>>()

    init {
        for (i in 1..width) {
            gameBoardArray.add(mutableList<T?>(width) { null })
        }
    }

    override fun get(cell: Cell): T? {
        var arr = gameBoardArray[cell.i - 1];
        return arr.get(cell.j - 1)
    }

    override fun set(cell: Cell, value: T?) {
        var list = mutableList<T?>(0) { null }
        var sizeTempList = gameBoardArray.get(cell.i - 1)
        for (item in sizeTempList) {
            list.add(item)
        }
        gameBoardArray.set(cell.i - 1, mutableList<T?>(width) { null });
        list.set(cell.j - 1, value)
        gameBoardArray.set(cell.i - 1, list)
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        var value = gameBoardArray
                .flatMap { it }
                .filter(predicate)
                .firstOrNull()
        var result = mutableListOf<Cell>()
        for (i in 0..gameBoardArray.size - 1) {
            for (j in 0..gameBoardArray[i].size - 1) {
                var valueI = gameBoardArray[i]
                var valueJ = valueI[j];
                    if (valueJ == value) {
                        result.add(getCell(i + 1, j + 1))
                    }
            }
        }


        return result
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        var value = gameBoardArray
                .flatMap { it }
                .filter(predicate)
                .firstOrNull()
        var result = mutableListOf<Cell>()
        for (i in 0..gameBoardArray.size - 1) {
            for (j in 0..gameBoardArray[i].size - 1) {
                var valueI = gameBoardArray[i]
                var valueJ = valueI[j];

                if (valueJ != null) {
                    if (valueJ.equals(value)) {
                        result.add(getCell(i + 1, j + 1))
                    }
                }
            }
        }

        return result.first()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return gameBoardArray
                .flatMap { it }
                .any(predicate)
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return gameBoardArray
                .flatMap { it }
                .all(predicate)
    }
}

fun createSquareBoard(width: Int): SquareBoard {
    var temp = BoardImpl(width);
    return BoardImpl(width)
}

fun <T> createGameBoard(width: Int): GameBoard<T> {
    return GameBoardImpl<T>(width)

}

