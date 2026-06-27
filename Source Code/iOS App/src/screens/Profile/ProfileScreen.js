import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
  Image,
  Alert,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

import {Card, Button, Input, Modal} from '../../components';
import {userService} from '../../services';
import {useSelector, useDispatch} from 'react-redux';
import {logout} from '../../store/slices/authSlice';
import {updateUser} from '../../store/slices/authSlice';

const ProfileScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const {user} = useSelector(state => state.auth);
  const [loading, setLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editData, setEditData] = useState({
    name: '',
    email: '',
    phone: '',
  });

  useEffect(() => {
    if (user) {
      setEditData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
      });
    }
  }, [user]);

  const handleLogout = () => {
    Alert.alert(
      'تسجيل الخروج',
      'هل أنت متأكد من تسجيل الخروج؟',
      [
        {text: 'إلغاء', style: 'cancel'},
        {
          text: 'تسجيل الخروج',
          style: 'destructive',
          onPress: () => {
            dispatch(logout());
            navigation.reset({
              index: 0,
              routes: [{name: 'Auth'}],
            });
          },
        },
      ]
    );
  };

  const handleEditProfile = () => {
    setEditModalVisible(true);
  };

  const handleSaveProfile = async () => {
    try {
      setLoading(true);
      await userService.updateProfile(editData);
      dispatch(updateUser(editData));
      setEditModalVisible(false);
      Alert.alert('نجاح', 'تم تحديث الملف الشخصي');
    } catch (error) {
      Alert.alert('خطأ', 'فشل تحديث الملف الشخصي');
    } finally {
      setLoading(false);
    }
  };

  const menuItems = [
    {
      icon: 'person-outline',
      title: 'تعديل الملف الشخصي',
      onPress: handleEditProfile,
    },
    {
      icon: 'shield-outline',
      title: 'الأمان والخصوصية',
      onPress: () => navigation.navigate('Security'),
    },
    {
      icon: 'notifications-outline',
      title: 'الإشعارات',
      onPress: () => navigation.navigate('Notifications'),
    },
    {
      icon: 'help-circle-outline',
      title: 'المساعدة والدعم',
      onPress: () => navigation.navigate('Help'),
    },
    {
      icon: 'document-text-outline',
      title: 'الشروط والأحكام',
      onPress: () => navigation.navigate('Terms'),
    },
    {
      icon: 'information-circle-outline',
      title: 'عن التطبيق',
      onPress: () => navigation.navigate('About'),
    },
  ];

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.header}>
        <View style={styles.headerContent}>
          <Text style={styles.title}>الملف الشخصي</Text>
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          {/* Profile Card */}
          <Card style={styles.profileCard}>
            <View style={styles.avatarContainer}>
              <View style={styles.avatar}>
                <Text style={styles.avatarText}>
                  {user?.name?.charAt(0).toUpperCase() || 'U'}
                </Text>
              </View>
              <TouchableOpacity style={styles.editAvatarButton}>
                <Icon name="camera" size={20} color="#fff" />
              </TouchableOpacity>
            </View>
            <Text style={styles.userName}>{user?.name || 'مستخدم'}</Text>
            <Text style={styles.userEmail}>{user?.email || ''}</Text>
            <Text style={styles.memberSince}>عضو منذ 2024</Text>
          </Card>

          {/* Statistics */}
          <View style={styles.statsContainer}>
            <Card style={styles.statCard}>
              <Text style={styles.statValue}>156</Text>
              <Text style={styles.statLabel">الألعاب</Text>
            </Card>
            <Card style={styles.statCard}>
              <Text style={styles.statValue}>45</Text>
              <Text style={styles.statLabel">الفوز</Text>
            </Card>
            <Card style={styles.statCard}>
              <Text style={styles.statValue}>89%</Text>
              <Text style={styles.statLabel">النسبة</Text>
            </Card>
          </View>

          {/* Menu Items */}
          <View style={styles.menuSection}>
            {menuItems.map((item, index) => (
              <TouchableOpacity key={index} onPress={item.onPress}>
                <Card style={styles.menuItem}>
                  <View style={styles.menuItemLeft}>
                    <Icon name={item.icon} size={24} color="#e60a57" />
                    <Text style={styles.menuItemTitle}>{item.title}</Text>
                  </View>
                  <Icon name="chevron-forward" size={20} color="#ccc" />
                </Card>
              </TouchableOpacity>
            ))}
          </View>

          {/* Logout Button */}
          <Button
            title="تسجيل الخروج"
            onPress={handleLogout}
            variant="danger"
            style={styles.logoutButton}
          />
        </ScrollView>
      </View>

      {/* Edit Profile Modal */}
      <Modal
        visible={editModalVisible}
        onClose={() => setEditModalVisible(false)}
        title="تعديل الملف الشخصي">
        <Input
          label="الاسم"
          placeholder="أدخل اسمك"
          value={editData.name}
          onChangeText={text => setEditData({...editData, name: text})}
          icon="person-outline"
        />
        <Input
          label="البريد الإلكتروني"
          placeholder="أدخل بريدك الإلكتروني"
          value={editData.email}
          onChangeText={text => setEditData({...editData, email: text})}
          keyboardType="email-address"
          icon="mail-outline"
        />
        <Input
          label="رقم الهاتف"
          placeholder="أدخل رقم هاتفك"
          value={editData.phone}
          onChangeText={text => setEditData({...editData, phone: text})}
          keyboardType="phone-pad"
          icon="call-outline"
        />
        <Button
          title="حفظ التغييرات"
          onPress={handleSaveProfile}
          loading={loading}
        />
      </Modal>
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
    fontSize: 24,
    fontWeight: 'bold',
        color: '#fff',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  profileCard: {
    alignItems: 'center',
    padding: 25,
    marginBottom: 20,
  },
  avatarContainer: {
    position: 'relative',
    marginBottom: 15,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#e60a57',
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarText: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#fff',
  },
  editAvatarButton: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    width: 30,
    height: 30,
    borderRadius: 15,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 3,
  },
  userName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 5,
  },
  userEmail: {
    fontSize: 14,
    color: '#666',
    marginBottom: 5,
  },
  memberSince: {
    fontSize: 12,
    color: '#888',
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 20,
  },
  statCard: {
    flex: 1,
    alignItems: 'center',
    padding: 15,
    marginHorizontal: 5,
  },
  statValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#e60a57',
    marginBottom: 5,
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
  },
  menuSection: {
    marginBottom: 20,
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 15,
    marginBottom: 10,
  },
  menuItemLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  menuItemTitle: {
    fontSize: 16,
    color: '#333',
    marginLeft: 15,
  },
  logoutButton: {
    marginBottom: 20,
  },
});

export default ProfileScreen;