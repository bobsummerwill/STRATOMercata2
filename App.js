import React from 'react';
import { StyleSheet, SafeAreaView, ScrollView, StatusBar, View } from 'react-native';
import Header from './components/Header';
import GoldPrice from './components/GoldPrice';
import PriceChart from './components/PriceChart';
import TradeForm from './components/TradeForm';

export default function App() {
  return (
    <>
      <StatusBar barStyle="dark-content" backgroundColor="#FFFFFF" />
      <SafeAreaView style={styles.container}>
        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <Header />
          <View style={styles.contentContainer}>
            <GoldPrice />
            <PriceChart />
            <TradeForm />
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  scrollContainer: {
    alignItems: 'center',
    paddingBottom: 30,
  },
  contentContainer: {
    width: '100%',
    alignItems: 'center',
    paddingTop: 10,
  },
});
