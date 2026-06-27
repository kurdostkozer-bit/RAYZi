import React from 'react';
import {
  View,
  StyleSheet,
  TouchableOpacity,
  ViewStyle,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

const Card = ({
  children,
  style,
  onPress,
  gradient = false,
  gradientColors = ['#ffffff', '#f5f5f5'],
  shadow = true,
  borderRadius = 10,
  padding = 15,
}) => {
  const cardStyle: ViewStyle = [
    styles.card,
    {
      borderRadius,
      padding,
    },
    shadow && styles.shadow,
    style,
  ];

  if (gradient) {
    return (
      <TouchableOpacity
        onPress={onPress}
        activeOpacity={onPress ? 0.8 : 1}
        disabled={!onPress}>
        <LinearGradient
          colors={gradientColors}
          start={{x: 0, y: 0}}
          end={{x: 1, y: 1}}
          style={cardStyle}>
          {children}
        </LinearGradient>
      </TouchableOpacity>
    );
  }

  if (onPress) {
    return (
      <TouchableOpacity
        onPress={onPress}
        activeOpacity={0.8}
        style={cardStyle}>
        {children}
      </TouchableOpacity>
    );
  }

  return <View style={cardStyle}>{children}</View>;
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#eee',
  },
  shadow: {
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
});

export default Card;