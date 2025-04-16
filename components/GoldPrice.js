import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';

export default function GoldPrice() {
  const [price, setPrice] = useState(1923.45);
  const [change, setChange] = useState(12.30);
  const [isPositive, setIsPositive] = useState(true);

  // Simulate price changes
  useEffect(() => {
    const interval = setInterval(() => {
      // Generate random price fluctuations
      const fluctuation = (Math.random() * 5) - 2.5; // Between -2.5 and 2.5
      const newPrice = parseFloat((price + fluctuation).toFixed(2));
      const newChange = parseFloat((fluctuation).toFixed(2));
      
      setPrice(newPrice);
      setChange(newChange);
      setIsPositive(newChange >= 0);
    }, 5000);

    return () => clearInterval(interval);
  }, [price]);

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.label}>Current Gold Price (USD/oz)</Text>
        <View style={styles.goldIndicator}>
          <Text style={styles.goldText}>GOLD</Text>
        </View>
      </View>
      <View style={styles.priceContainer}>
        <Text style={styles.price}>${price.toFixed(2)}</Text>
        <Text style={[
          styles.change, 
          isPositive ? styles.positive : styles.negative
        ]}>
          {isPositive ? '+' : ''}{change.toFixed(2)} ({isPositive ? '+' : ''}
          {((change / (price - change)) * 100).toFixed(2)}%)
        </Text>
      </View>
      <View style={styles.coinImagePlaceholder}>
        <View style={styles.coinStack}></View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    borderRadius: 10,
    marginVertical: 15,
    width: '90%',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    borderWidth: 1,
    borderColor: '#EEEEEE',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: '100%',
    marginBottom: 15,
  },
  label: {
    color: '#555555',
    fontSize: 16,
    fontWeight: 'bold',
  },
  goldIndicator: {
    backgroundColor: '#0066FF',
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 5,
  },
  goldText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 12,
  },
  priceContainer: {
    alignItems: 'center',
    marginBottom: 15,
  },
  price: {
    color: '#000000',
    fontSize: 36,
    fontWeight: 'bold',
  },
  change: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 5,
  },
  positive: {
    color: '#4caf50',
  },
  negative: {
    color: '#f44336',
  },
  coinImagePlaceholder: {
    width: '100%',
    alignItems: 'center',
    marginTop: 10,
  },
  coinStack: {
    width: 80,
    height: 80,
    backgroundColor: '#FFD700',
    borderRadius: 40,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 5,
  }
});
