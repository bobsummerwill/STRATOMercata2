import React from 'react';
import { View, StyleSheet, Dimensions } from 'react-native';

const BOARD_WIDTH = 10;
const BLOCK_SIZE = Dimensions.get('window').width / BOARD_WIDTH / 1.5;

export default function Block({ value }) {
  return (
    <View
      style={[
        styles.block,
        value === 0 ? styles.empty : value === 1 ? styles.filled : styles.active,
      ]}
    />
  );
}

const styles = StyleSheet.create({
  block: {
    width: BLOCK_SIZE,
    height: BLOCK_SIZE,
    borderWidth: 1,
    borderColor: '#333',
  },
  empty: {
    backgroundColor: '#000',
  },
  filled: {
    backgroundColor: '#555',
  },
  active: {
    backgroundColor: '#f00',
  },
}); 