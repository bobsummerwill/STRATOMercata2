import React, { useState, useEffect } from 'react';
import { View, StyleSheet, Dimensions, TouchableOpacity, Text, BackHandler } from 'react-native';
import Block from './Block';

const BOARD_WIDTH = 10;
const BOARD_HEIGHT = 20;
const BLOCK_SIZE = Dimensions.get('window').width / BOARD_WIDTH / 1.5;

export default function GameBoard() {
  const [board, setBoard] = useState(createEmptyBoard());
  const [currentPiece, setCurrentPiece] = useState(null);
  const [score, setScore] = useState(0);
  const [gameOver, setGameOver] = useState(false);

  useEffect(() => {
    if (!currentPiece) {
      spawnNewPiece();
    }

    const gameLoop = setInterval(() => {
      if (!gameOver) {
        moveDown();
      }
    }, 1000);

    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      if (gameOver) {
        setBoard(createEmptyBoard());
        setScore(0);
        setGameOver(false);
        setCurrentPiece(null);
        return true;
      }
      return false;
    });

    return () => {
      clearInterval(gameLoop);
      backHandler.remove();
    };
  }, [currentPiece, gameOver]);

  function createEmptyBoard() {
    return Array(BOARD_HEIGHT).fill().map(() => Array(BOARD_WIDTH).fill(0));
  }

  function spawnNewPiece() {
    const pieces = [
      [[1, 1, 1, 1]], // I
      [[1, 1], [1, 1]], // O
      [[1, 1, 1], [0, 1, 0]], // T
      [[1, 1, 1], [1, 0, 0]], // L
      [[1, 1, 1], [0, 0, 1]], // J
      [[1, 1, 0], [0, 1, 1]], // S
      [[0, 1, 1], [1, 1, 0]], // Z
    ];

    const newPiece = {
      shape: pieces[Math.floor(Math.random() * pieces.length)],
      x: Math.floor(BOARD_WIDTH / 2) - 1,
      y: 0,
    };

    if (isColliding(newPiece)) {
      setGameOver(true);
    } else {
      setCurrentPiece(newPiece);
    }
  }

  function isColliding(piece) {
    return piece.shape.some((row, dy) =>
      row.some((value, dx) => {
        if (!value) return false;
        const newX = piece.x + dx;
        const newY = piece.y + dy;
        return (
          newX < 0 ||
          newX >= BOARD_WIDTH ||
          newY >= BOARD_HEIGHT ||
          (newY >= 0 && board[newY][newX])
        );
      })
    );
  }

  function moveDown() {
    if (!currentPiece) return;

    const newPiece = {
      ...currentPiece,
      y: currentPiece.y + 1,
    };

    if (isColliding(newPiece)) {
      mergePiece();
      clearLines();
      spawnNewPiece();
    } else {
      setCurrentPiece(newPiece);
    }
  }

  function moveLeft() {
    if (!currentPiece) return;

    const newPiece = {
      ...currentPiece,
      x: currentPiece.x - 1,
    };

    if (!isColliding(newPiece)) {
      setCurrentPiece(newPiece);
    }
  }

  function moveRight() {
    if (!currentPiece) return;

    const newPiece = {
      ...currentPiece,
      x: currentPiece.x + 1,
    };

    if (!isColliding(newPiece)) {
      setCurrentPiece(newPiece);
    }
  }

  function rotate() {
    if (!currentPiece) return;

    const newShape = currentPiece.shape[0].map((_, i) =>
      currentPiece.shape.map(row => row[row.length - 1 - i])
    );

    const newPiece = {
      ...currentPiece,
      shape: newShape,
    };

    if (!isColliding(newPiece)) {
      setCurrentPiece(newPiece);
    }
  }

  function mergePiece() {
    const newBoard = [...board];
    currentPiece.shape.forEach((row, dy) => {
      row.forEach((value, dx) => {
        if (value) {
          const y = currentPiece.y + dy;
          const x = currentPiece.x + dx;
          if (y >= 0) {
            newBoard[y][x] = 1;
          }
        }
      });
    });
    setBoard(newBoard);
  }

  function clearLines() {
    const newBoard = board.filter(row => row.some(cell => !cell));
    const clearedLines = BOARD_HEIGHT - newBoard.length;
    const newScore = score + (clearedLines * 100);
    
    setScore(newScore);
    setBoard([
      ...Array(clearedLines).fill().map(() => Array(BOARD_WIDTH).fill(0)),
      ...newBoard,
    ]);
  }

  function renderBoard() {
    const displayBoard = [...board];
    
    if (currentPiece) {
      currentPiece.shape.forEach((row, dy) => {
        row.forEach((value, dx) => {
          if (value) {
            const y = currentPiece.y + dy;
            const x = currentPiece.x + dx;
            if (y >= 0 && y < BOARD_HEIGHT && x >= 0 && x < BOARD_WIDTH) {
              displayBoard[y][x] = 2;
            }
          }
        });
      });
    }

    return displayBoard.map((row, y) => (
      <View key={y} style={styles.row}>
        {row.map((cell, x) => (
          <Block key={`${x}-${y}`} value={cell} />
        ))}
      </View>
    ));
  }

  return (
    <View style={styles.container}>
      <Text style={styles.score}>Score: {score}</Text>
      <View style={styles.board}>{renderBoard()}</View>
      {gameOver && (
        <Text style={styles.gameOver}>Game Over!</Text>
      )}
      <View style={styles.controls}>
        <TouchableOpacity style={styles.button} onPress={moveLeft}>
          <Text style={styles.buttonText}>←</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={moveDown}>
          <Text style={styles.buttonText}>↓</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={moveRight}>
          <Text style={styles.buttonText}>→</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={rotate}>
          <Text style={styles.buttonText}>↻</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  board: {
    borderWidth: 2,
    borderColor: '#333',
    backgroundColor: '#000',
  },
  row: {
    flexDirection: 'row',
  },
  score: {
    fontSize: 24,
    color: '#fff',
    marginBottom: 20,
  },
  gameOver: {
    fontSize: 32,
    color: 'red',
    position: 'absolute',
  },
  controls: {
    flexDirection: 'row',
    marginTop: 20,
  },
  button: {
    width: 60,
    height: 60,
    backgroundColor: '#333',
    margin: 8,
    borderRadius: 30,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 3,
  },
  buttonText: {
    color: '#fff',
    fontSize: 28,
  },
}); 