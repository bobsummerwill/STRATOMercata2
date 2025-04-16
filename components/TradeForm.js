import React, { useState } from 'react';
import { View, Text, StyleSheet, TextInput, TouchableOpacity } from 'react-native';

export default function TradeForm() {
  const [isBuying, setIsBuying] = useState(true);
  const [amount, setAmount] = useState('1.00');
  const [currentPrice] = useState(1923.45);

  const handleExecute = () => {
    const action = isBuying ? 'Bought' : 'Sold';
    const totalPrice = parseFloat(amount) * currentPrice;
    
    alert(`${action} ${amount} oz of gold for $${totalPrice.toFixed(2)}`);
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>{isBuying ? 'Buy' : 'Sell'} Gold</Text>
        <View style={styles.tradeIndicator}>
          <Text style={styles.tradeText}>TRADE</Text>
        </View>
      </View>
      
      <View style={styles.buttonContainer}>
        <TouchableOpacity 
          style={[styles.actionButton, isBuying ? styles.buyActiveButton : styles.inactiveButton]} 
          onPress={() => setIsBuying(true)}
        >
          <Text style={[styles.buttonText, isBuying && styles.activeButtonText]}>Buy</Text>
        </TouchableOpacity>
        
        <TouchableOpacity 
          style={[styles.actionButton, !isBuying ? styles.sellActiveButton : styles.inactiveButton]} 
          onPress={() => setIsBuying(false)}
        >
          <Text style={[styles.buttonText, !isBuying && styles.activeButtonText]}>Sell</Text>
        </TouchableOpacity>
      </View>
      
      <View style={styles.formSection}>
        <View style={styles.inputContainer}>
          <Text style={styles.label}>Amount (oz)</Text>
          <TextInput
            style={styles.input}
            value={amount}
            onChangeText={setAmount}
            keyboardType="numeric"
            placeholderTextColor="#999"
          />
        </View>
        
        <View style={styles.priceContainer}>
          <Text style={styles.label}>Price (USD)</Text>
          <View style={styles.priceBox}>
            <Text style={styles.price}>${(parseFloat(amount) * currentPrice).toFixed(2)}</Text>
          </View>
        </View>
      </View>
      
      <TouchableOpacity 
        style={[
          styles.executeButton, 
          isBuying ? styles.buyExecuteButton : styles.sellExecuteButton
        ]} 
        onPress={handleExecute}
      >
        <Text style={styles.executeButtonText}>Execute Trade</Text>
      </TouchableOpacity>
      
      <View style={styles.disclaimer}>
        <Text style={styles.disclaimerText}>
          Trading involves risk. Please ensure you understand the risks before trading.
        </Text>
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
    marginBottom: 20,
  },
  title: {
    color: '#000000',
    fontSize: 22,
    fontWeight: 'bold',
  },
  tradeIndicator: {
    backgroundColor: '#0066FF',
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 5,
  },
  tradeText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 12,
  },
  buttonContainer: {
    flexDirection: 'row',
    marginBottom: 25,
    backgroundColor: '#F5F5F5',
    borderRadius: 8,
    padding: 4,
  },
  actionButton: {
    flex: 1,
    padding: 12,
    alignItems: 'center',
    borderRadius: 6,
    marginHorizontal: 2,
  },
  buyActiveButton: {
    backgroundColor: '#4CAF50',
  },
  sellActiveButton: {
    backgroundColor: '#F44336',
  },
  inactiveButton: {
    backgroundColor: 'transparent',
  },
  buttonText: {
    color: '#555555',
    fontWeight: 'bold',
    fontSize: 16,
  },
  activeButtonText: {
    color: '#FFFFFF',
  },
  formSection: {
    backgroundColor: '#F9F9F9',
    borderRadius: 8,
    padding: 15,
    marginBottom: 20,
  },
  inputContainer: {
    marginBottom: 15,
  },
  label: {
    color: '#555555',
    marginBottom: 8,
    fontWeight: '500',
    fontSize: 14,
  },
  input: {
    backgroundColor: '#FFFFFF',
    color: '#000000',
    padding: 12,
    borderRadius: 6,
    textAlign: 'right',
    borderWidth: 1,
    borderColor: '#DDDDDD',
    fontSize: 16,
  },
  priceContainer: {
    marginBottom: 5,
  },
  priceBox: {
    backgroundColor: '#FFFFFF',
    padding: 12,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#DDDDDD',
  },
  price: {
    color: '#000000',
    textAlign: 'right',
    fontSize: 16,
    fontWeight: 'bold',
  },
  executeButton: {
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 15,
  },
  buyExecuteButton: {
    backgroundColor: '#4CAF50',
  },
  sellExecuteButton: {
    backgroundColor: '#F44336',
  },
  executeButtonText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 16,
  },
  disclaimer: {
    padding: 10,
    backgroundColor: '#F9F9F9',
    borderRadius: 6,
    borderLeftWidth: 3,
    borderLeftColor: '#0066FF',
  },
  disclaimerText: {
    color: '#666666',
    fontSize: 12,
    fontStyle: 'italic',
  }
});
