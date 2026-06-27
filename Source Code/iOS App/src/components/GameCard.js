import React from 'react';
import {View, Text, StyleSheet, Image, TouchableOpacity} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

const GameCard = ({
  title,
  description,
  icon,
  image,
  playersOnline,
  onPress,
  isLocked = false,
}) => {
  return (
    <TouchableOpacity onPress={onPress} activeOpacity={0.8} disabled={isLocked}>
      <LinearGradient
        colors={isLocked ? ['#999', '#777'] : ['#e60a57', '#ff4785']}
        start={{x: 0, y: 0}}
        end={{x: 1, y: 1}}
        style={styles.card}>
        <View style={styles.content}>
          <View style={styles.iconContainer}>
            {image ? (
              <Image source={{uri: image}} style={styles.image} />
            ) : (
              <Icon name={icon} size={40} color="#fff" />
            )}
          </View>
          <View style={styles.textContainer}>
            <Text style={styles.title}>{title}</Text>
            <Text style={styles.description}>{description}</Text>
            <View style={styles.infoContainer}>
              <View style={styles.infoItem}>
                <Icon name="people" size={16} color="#fff" />
                <Text style={styles.infoText}>{playersOnline} متصل</Text>
              </View>
              {isLocked && (
                <View style={styles.lockedBadge}>
                  <Icon name="lock-closed" size={14} color="#fff" />
                  <Text style={styles.lockedText}>مغلق</Text>
                </View>
              )}
            </View>
          </View>
          <Icon name="chevron-forward" size={24} color="#fff" />
        </View>
      </LinearGradient>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    borderRadius: 15,
    padding: 20,
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 5,
  },
  content: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  iconContainer: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 15,
  },
  image: {
    width: 50,
    height: 50,
    borderRadius: 25,
  },
  textContainer: {
    flex: 1,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  description: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.9)',
    marginBottom: 10,
  },
  infoContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  infoItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 15,
  },
  infoText: {
    fontSize: 12,
    color: '#fff',
    marginLeft: 5,
  },
  lockedBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    marginLeft: 10,
  },
  lockedText: {
    fontSize: 11,
    color: '#fff',
    marginLeft: 4,
  },
});

export default GameCard;