import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  StatusBar,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

import {GameCard} from '../../components';
import {FEATURES} from '../../utils/config';

const GamesListScreen = ({navigation}) => {
  const games = [
    {
      id: 'teenpatti',
      title: 'Teen Patti',
      description: 'لعبة الورق الهندية الشهيرة - اشترك الآن واربح!',
      icon: 'card-outline',
      playersOnline: 1234,
      enabled: FEATURES.TEENPATTI,
      image: null,
    },
    {
      id: 'roulette',
      title: 'Roulette Casino',
      description: 'لعبة الروليت الكلاسيكية - جرب حظك!',
      icon: 'grid-outline',
      playersOnline: 856,
      enabled: FEATURES.ROULETTE,
      image: null,
    },
    {
      id: 'ferrywheel',
      title: 'عجلة الحظ',
      description: 'أدر العجلة واربح الجوائز الكبرى!',
      icon: 'ribbon-outline',
      playersOnline: 432,
      enabled: FEATURES.FERRYWHEEL,
      image: null,
    },
  ];

  const handleGamePress = (game) => {
    if (!game.enabled) return;
    
    switch (game.id) {
      case 'teenpatti':
        navigation.navigate('TeenPatti');
        break;
      case 'roulette':
        navigation.navigate('Roulette');
        break;
      case 'ferrywheel':
        navigation.navigate('FerryWheel');
        break;
    }
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.header}>
        <View style={styles.headerContent}>
          <Text style={styles.title}>الألعاب</Text>
          <Text style={styles.subtitle}>اختر لعبتك المفضلة</Text>
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          {games.map(game => (
            <GameCard
              key={game.id}
              title={game.title}
              description={game.description}
              icon={game.icon}
              image={game.image}
              playersOnline={game.playersOnline}
              onPress={() => handleGamePress(game)}
              isLocked={!game.enabled}
            />
          ))}

          {/* Coming Soon Games */}
          <View style={styles.comingSoon}>
            <Text style={styles.comingSoonTitle}>قريباً</Text>
            <GameCard
              title="بلاك جاك"
              description="لعبة البلاك جاك الكلاسيكية"
              icon="diamond-outline"
              playersOnline={0}
              isLocked={true}
            />
            <GameCard
              title="بوكر"
              description="لعبة البوكر الشهيرة"
              icon="sparkles-outline"
              playersOnline={0}
              isLocked={true}
            />
          </View>
        </ScrollView>
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  header: {
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerContent: {
    alignItems: 'center',
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  subtitle: {
    fontSize: 16,
    color: 'rgba(255, 255, 255, 0.9)',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  comingSoon: {
    marginTop: 20,
  },
  comingSoonTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#888',
    marginBottom: 15,
    textAlign: 'center',
  },
});

export default GamesListScreen;