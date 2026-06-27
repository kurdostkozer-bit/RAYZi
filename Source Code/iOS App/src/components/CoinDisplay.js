import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';

const CoinDisplay = ({coins, size = 'medium', showLabel = false}) => {
  const getIconSize = () => {
    switch (size) {
      case 'small':
        return 16;
      case 'medium':
        return 24;
      case 'large':
        return 32;
      default:
        return 24;
    }
  };

  const getFontSize = () => {
    switch (size) {
      case 'small':
        return 12;
      case 'medium':
        return 16;
      case 'large':
        return 20;
      default:
        return 16;
    }
  };

  const formatCoins = (value) => {
    if (value >= 1000000) {
      return (value / 1000000).toFixed(1) + 'M';
    } else if (value >= 1000) {
      return (value / 1000).toFixed(1) + 'K';
    }
    return value.toString();
  };

  return (
    <View style={styles.container}>
      <Icon name="cash" size={getIconSize()} color="#ffd700" />
      <Text style={[styles.coinsText, {fontSize: getFontSize()}]}>
        {formatCoins(coins)}
      </Text>
      {showLabel && <Text style={styles.label}>عملات</Text>}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.05)',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
  },
  coinsText: {
    fontWeight: 'bold',
    color: '#333',
    marginLeft: 5,
  },
  label: {
    fontSize: 12,
    color: '#666',
    marginLeft: 5,
  },
});

export default CoinDisplay;