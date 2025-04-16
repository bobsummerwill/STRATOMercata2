import React from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';

export default function Header() {
  return (
    <View style={styles.header}>
      <View style={styles.titleContainer}>
        <Text style={styles.strato}>STRATO</Text>
        <Text style={styles.mercata}>MERCATA</Text>
      </View>
      <Text style={styles.subtitle}>Gold Trading Platform</Text>
      <View style={styles.logoContainer}>
        <View style={styles.logo}>
          <Text style={styles.logoText}>GOLDST</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    width: '100%',
    padding: 20,
    backgroundColor: '#FFFFFF',
    alignItems: 'center',
    justifyContent: 'center',
    borderBottomWidth: 2,
    borderBottomColor: '#0066FF',
  },
  titleContainer: {
    flexDirection: 'column',
    alignItems: 'center',
  },
  strato: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#0066FF',
    letterSpacing: 1,
  },
  mercata: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#000000',
    letterSpacing: 1,
  },
  subtitle: {
    fontSize: 16,
    color: '#555555',
    marginTop: 5,
  },
  logoContainer: {
    position: 'absolute',
    right: 20,
    top: 20,
  },
  logo: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: '#0066FF',
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 2,
    borderColor: '#FF3333',
  },
  logoText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 12,
  }
});
