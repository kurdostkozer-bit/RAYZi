import React, {useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
  Image,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {useSelector} from 'react-redux';
import Icon from 'react-native-vector-icons/Ionicons';

import {GameCard, CoinDisplay} from '../../components';
import {FEATURES} from '../../utils/config';

const HomeScreen = ({navigation}) => {
  const {user} = useSelector(state => state.auth);
  const {coins} = useSelector(state => state.wallet);

  const games = [
    {
      id: 'teenpatti',
      title: 'Teen Patti',
      description: 'لعبة الورق الهندية الشهيرة',
      icon: 'card-outline',
      playersOnline: 1234,
      enabled: FEATURES.TEENPATTI,
    },
    {
      id: 'roulette',
      title: 'Roulette Casino',
      description: 'لعبة الروليت الكلاسيكية',
      icon: 'grid-outline',
      playersOnline: 856,
      enabled: FEATURES.ROULETTE,
    },
    {
      id: 'ferrywheel',
      title: 'عجلة الحظ',
      description: 'جرب حظك وأربح الجوائز',
      icon: 'ribbon-outline',
      playersOnline: 432,
      enabled: FEATURES.FERRYWHEEL,
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
      <StatusBar barStyle="dark-content" backgroundColor="#fff" />
      <View style={styles.container}>
        {/* Header */}
        <LinearGradient
          colors={['#e60a57', '#ff4785']}
          style={styles.header}>
          <View style={styles.headerContent}>
            <View style={styles.userInfo}>
              <View style={styles.avatar}>
                <Text style={styles.avatarText}>
                  {user?.name?.charAt(0).toUpperCase() || 'U'}
                </Text>
              </View>
              <View style={styles.userDetails}>
                <Text style={styles.userName}>{user?.name || 'مرحباً'}</Text>
                <Text style={styles.userEmail}>{user?.email || ''}</Text>
              </View>
            </View>
            <TouchableOpacity style={styles.notificationButton}>
              <Icon name="notifications-outline" size={24} color="#fff" />
              <View style={styles.notificationBadge} />
            </TouchableOpacity>
          </View>

          <View style={styles.balanceContainer}>
            <CoinDisplay coins={coins || 0} size="large" showLabel />
            <TouchableOpacity style={styles.addCoinsButton}>
              <Icon name="add-circle" size={24} color="#fff" />
              <Text style={styles.addCoinsText}>إضافة عملات</Text>
            </TouchableOpacity>
          </View>
        </LinearGradient>

        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          {/* Banner */}
          <View style={styles.banner}>
            <LinearGradient
              colors={['#ff6b6b', '#ee5a5a']}
              style={styles.bannerGradient}>
              <View style={styles.bannerContent}>
                <View style={styles.bannerText}>
                  <Text style={styles.bannerTitle}>مكافأة يومية!</Text>
                  <Text style={styles.bannerSubtitle}>احصل على 500 عملة مجانية يومياً</Text>
                </View>
                <TouchableOpacity style={styles.claimButton}>
                  <Text style={styles.claimButtonText}>اطلب الآن</Text>
                </TouchableOpacity>
              </View>
              <Icon name="gift" size={60} color="rgba(255, 255, 255, 0.3)" />
            </LinearGradient>
          </View>

          {/* Games Section */}
          <View style={styles.section}>
            <View style={styles.sectionHeader}>
              <Text style={styles.sectionTitle}>الألعاب المتاحة</Text>
              <TouchableOpacity onPress={() => navigation.navigate('GamesList')}>
                <Text style={styles.seeAllText}>عرض الكل</Text>
              </TouchableOpacity>
            </View>

            {games.map(game => (
              <GameCard
                key={game.id}
                title={game.title}
                description={game.description}
                icon={game.icon}
                playersOnline={game.playersOnline}
                onPress={() => handleGamePress(game)}
                isLocked={!game.enabled}
              />
            ))}
          </View>

          {/* Quick Actions */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>إجراءات سريعة</Text>
            <View style={styles.quickActions}>
              <TouchableOpacity style={styles.quickAction}>
                <View style={styles.quickActionIcon}>
                  <Icon name="wallet-outline" size={24} color="#e60a57" />
                </View>
                <Text style={styles.quickActionText}>المحفظة</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.quickAction}>
                <View style={styles.quickActionIcon}>
                  <Icon name="people-outline" size={24} color="#e60a57" />
                </View>
                <Text style={styles.quickActionText}>الأصدقاء</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.quickAction}>
                <View style={styles.quickActionIcon}>
                  <Icon name="trophy-outline" size={24} color="#e60a57" />
                </View>
                <Text style={styles.quickActionText}>التصنيف</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.quickAction}>
                <View style={styles.quickActionIcon}>
                  <Icon name="settings-outline" size={24} color="#e60a57" />
                </View>
                <Text style={styles.quickActionText}>الإعدادات</Text>
              </TouchableOpacity>
            </View>
          </View>

          {/* Online Players */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>اللاعبون المتصلون</Text>
            <View style={styles.onlinePlayers}>
              <View style={styles.onlinePlayer}>
                <View style={styles.onlinePlayerAvatar}>
                  <Text style={styles.onlinePlayerAvatarText}>A</Text>
                </View>
                <View style={styles.onlinePlayerInfo}>
                  <Text style={styles.onlinePlayerName}>أحمد</Text>
                  <Text style={styles.onlinePlayerStatus}>يلعب Teen Patti</Text>
                </View>
              </View>
              <View style={styles.onlinePlayer}>
                <View style={styles.onlinePlayerAvatar}>
                  <Text style={styles.onlinePlayerAvatarText}>M</Text>
                </View>
                <View style={styles.onlinePlayerInfo}>
                  <Text style={styles.onlinePlayerName}>محمد</Text>
                  <Text style={styles.onlinePlayerStatus}>يلعب Roulette</Text>
                </View>
              </View>
            </View>
          </View>
        </ScrollView>
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  avatar: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 15,
  },
  avatarText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
  },
  userDetails: {
    flex: 1,
  },
  userName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#fff',
  },
  userEmail: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.9)',
  },
  notificationButton: {
    position: 'relative',
  },
  notificationBadge: {
    position: 'absolute',
    top: 0,
    right: 0,
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#ff3b30',
  },
  balanceContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  addCoinsButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 20,
  },
  addCoinsText: {
    color: '#fff',
    fontWeight: '600',
    marginLeft: 5,
  },
  content: {
    flex: 1,
  },
  banner: {
    margin: 20,
    borderRadius: 15,
    overflow: 'hidden',
  },
  bannerGradient: {
    padding: 20,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  bannerContent: {
    flex: 1,
  },
  bannerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  bannerSubtitle: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.9)',
    marginBottom: 15,
  },
  claimButton: {
    backgroundColor: '#fff',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 20,
    alignSelf: 'flex-start',
  },
  claimButtonText: {
    color: '#ff6b6b',
    fontWeight: 'bold',
  },
  section: {
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 15,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  seeAllText: {
    fontSize: 14,
    color: '#e60a57',
    fontWeight: '600',
  },
  quickActions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  quickAction: {
    alignItems: 'center',
    flex: 1,
  },
  quickActionIcon: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 8,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  quickActionText: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
  onlinePlayers: {
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 15,
  },
  onlinePlayer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  onlinePlayerAvatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: '#e60a57',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  onlinePlayerAvatarText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#fff',
  },
  onlinePlayerInfo: {
    flex: 1,
  },
  onlinePlayerName: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  onlinePlayerStatus: {
    fontSize: 12,
    color: '#888',
  },
});

export default HomeScreen;