import React, { useEffect, useMemo } from 'react';
import { StyleSheet, View, Dimensions } from 'react-native';
import Animated, { 
  useSharedValue, 
  useAnimatedStyle, 
  withTiming, 
  withRepeat, 
  withSequence,
  Easing,
  cancelAnimation
} from 'react-native-reanimated';

const { width, height } = Dimensions.get('window');

// Generate random candlestick data
const generateCandlesticks = (count: number) => {
  const candlesticks = [];
  let lastClose = 100 + Math.random() * 50;
  
  for (let i = 0; i < count; i++) {
    const open = lastClose;
    const close = open * (0.95 + Math.random() * 0.1); // Random close price
    const high = Math.max(open, close) * (1 + Math.random() * 0.03);
    const low = Math.min(open, close) * (1 - Math.random() * 0.03);
    const isUp = close >= open;
    
    candlesticks.push({
      open,
      close,
      high,
      low,
      isUp,
      x: i * 20, // Horizontal spacing
    });
    
    lastClose = close;
  }
  
  return candlesticks;
};

// Candlestick component
const Candlestick = ({ data, scale = 1 }) => {
  const { open, close, high, low, isUp, x } = data;
  const color = isUp ? '#4CAF50' : '#F44336'; // Green for up, red for down
  
  const bodyHeight = Math.abs(open - close) * scale;
  const wickHeight = (high - low) * scale;
  const wickTop = (high - Math.max(open, close)) * scale;
  
  return (
    <View style={[styles.candlestick, { left: x }]}>
      {/* Wick */}
      <View style={[styles.wick, { height: wickHeight, backgroundColor: color }]} />
      
      {/* Body */}
      <View style={[
        styles.body, 
        { 
          height: bodyHeight, 
          backgroundColor: color,
          top: wickTop
        }
      ]} />
    </View>
  );
};

interface CandlestickBackgroundProps {
  opacity?: number;
}

const CandlestickBackground: React.FC<CandlestickBackgroundProps> = ({ opacity = 0.1 }) => {
  // Generate multiple candlestick charts with useMemo to prevent regeneration on re-renders
  const candlesticks1 = useMemo(() => generateCandlesticks(30), []);
  const candlesticks2 = useMemo(() => generateCandlesticks(25), []);
  
  // Animation values
  const translateX1 = useSharedValue(0);
  const translateX2 = useSharedValue(width * 0.5);
  
  // Set up animations
  useEffect(() => {
    // First chart animation
    translateX1.value = withRepeat(
      withSequence(
        withTiming(-width * 1.5, { duration: 15000, easing: Easing.linear }),
        withTiming(0, { duration: 0 })
      ),
      -1, // Infinite repeat
      false
    );
    
    // Second chart animation (offset timing)
    translateX2.value = withRepeat(
      withSequence(
        withTiming(-width * 1.5, { duration: 18000, easing: Easing.linear }),
        withTiming(width * 0.5, { duration: 0 })
      ),
      -1, // Infinite repeat
      false
    );
    
    return () => {
      // Clean up animations when component unmounts
      cancelAnimation(translateX1);
      cancelAnimation(translateX2);
    };
  }, []);
  
  // Animated styles
  const animatedStyle1 = useAnimatedStyle(() => {
    return {
      transform: [{ translateX: translateX1.value }],
    };
  });
  
  const animatedStyle2 = useAnimatedStyle(() => {
    return {
      transform: [{ translateX: translateX2.value }],
    };
  });
  
  return (
    <View style={[styles.container, { opacity }]}>
      <Animated.View style={[styles.chartContainer, animatedStyle1]}>
        {candlesticks1.map((data, index) => (
          <Candlestick key={`chart1-${index}`} data={data} scale={2} />
        ))}
      </Animated.View>
      
      <Animated.View style={[styles.chartContainer, animatedStyle2]}>
        {candlesticks2.map((data, index) => (
          <Candlestick key={`chart2-${index}`} data={data} scale={1.5} />
        ))}
      </Animated.View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    overflow: 'hidden',
  },
  chartContainer: {
    position: 'absolute',
    flexDirection: 'row',
    height: height * 2,
    width: width * 2,
  },
  candlestick: {
    position: 'absolute',
    alignItems: 'center',
    width: 10,
  },
  wick: {
    width: 1,
    position: 'absolute',
  },
  body: {
    width: 6,
    position: 'absolute',
  },
});

export default CandlestickBackground;