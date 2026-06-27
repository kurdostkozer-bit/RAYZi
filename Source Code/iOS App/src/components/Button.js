import React from 'react';
import {
  TouchableOpacity,
  Text,
  StyleSheet,
  ActivityIndicator,
  View,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

const Button = ({
  title,
  onPress,
  loading = false,
  disabled = false,
  variant = 'primary',
  size = 'medium',
  style,
  textStyle,
  icon,
}) => {
  const getGradientColors = () => {
    switch (variant) {
      case 'primary':
        return ['#e60a57', '#ff4785'];
      case 'secondary':
        return ['#4a90e2', '#357abd'];
      case 'success':
        return ['#2ecc71', '#27ae60'];
      case 'danger':
        return ['#e74c3c', '#c0392b'];
      case 'warning':
        return ['#f39c12', '#d68910'];
      default:
        return ['#e60a57', '#ff4785'];
    }
  };

  const getHeight = () => {
    switch (size) {
      case 'small':
        return 40;
      case 'medium':
        return 50;
      case 'large':
        return 60;
      default:
        return 50;
    }
  };

  const getFontSize = () => {
    switch (size) {
      case 'small':
        return 14;
      case 'medium':
        return 16;
      case 'large':
        return 18;
      default:
        return 16;
    }
  };

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled || loading}
      style={[styles.container, style]}
      activeOpacity={0.8}>
      <LinearGradient
        colors={getGradientColors()}
        start={{x: 0, y: 0}}
        end={{x: 1, y: 0}}
        style={[
          styles.button,
          {height: getHeight(), opacity: disabled || loading ? 0.6 : 1},
        ]}>
        {loading ? (
          <ActivityIndicator color="#fff" size="small" />
        ) : (
          <View style={styles.content}>
            {icon && <View style={styles.iconContainer}>{icon}</View>}
            <Text
              style={[
                styles.text,
                {fontSize: getFontSize()},
                icon && styles.textWithIcon,
                textStyle,
              ]}>
              {title}
            </Text>
          </View>
        )}
      </LinearGradient>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    borderRadius: 10,
    overflow: 'hidden',
  },
  button: {
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 10,
  },
  content: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    color: '#fff',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  textWithIcon: {
    marginLeft: 8,
  },
  iconContainer: {
    marginRight: 4,
  },
});

export default Button;