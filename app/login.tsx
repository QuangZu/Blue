import React from 'react';
import { Stack } from 'expo-router';
import LoginScreen from '../src/screens/Auth/LoginScreen';
import CandlestickBackground from '../src/components/CandlestickBackground';
import { View, StyleSheet } from 'react-native';

export default function Login() {
  return (
    <View style={styles.container}>
      <CandlestickBackground opacity={0.08} />
      <Stack.Screen options={{ headerShown: false }} />
      <LoginScreen />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});