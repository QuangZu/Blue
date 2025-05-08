import React from 'react';
import { Stack } from 'expo-router';
import RegisterScreen from '../src/screens/Auth/RegisterScreen';
import CandlestickBackground from '../src/components/CandlestickBackground';
import { View, StyleSheet } from 'react-native';

export default function Register() {
  return (
    <View style={styles.container}>
      <CandlestickBackground opacity={0.08} />
      <Stack.Screen options={{ headerShown: false }} />
      <RegisterScreen />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});