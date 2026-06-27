import React, {useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  StatusBar,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {useDispatch} from 'react-redux';
// تصحيح المسار واستدعاء الدالة الصحيحة لجلب التوكن بدقة
import {getToken} from '../services/api';
import {loginSuccess} from '../store/slices/authSlice';
import socketService from '../services/socketService';

const SplashScreen = ({navigation}) => {
  const dispatch = useDispatch();

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      // إبقاء شعار اللعبة لمدة ثانيتين لتجربة مستخدم سلسة
      await new Promise(resolve => setTimeout(resolve, 2000));

      // جلب التوكن المخزن في جهاز الآيفون إن وجد
      const token = await getToken();
      
      if (token) {
        // إذا وجد التوكن، نقوم بتسجيل دخول اللاعب تلقائياً وتفعيل السوكيت الحي
        dispatch(loginSuccess({
          token,
          user: {id: '1', name: 'لاعب رايزي', email: 'user@example.com'} // سيتم تحديثها تلقائياً من الـ Profile لاحقاً
        }));
        
        // تشغيل اتصال السوكيت الرئيسي للعبة فوراً عند الإقلاع
        try {
          await socketService.connect();
        } catch (socketError) {
          console.warn('⚠️ Main socket connection deferred:', socketError.message);
        }
        
        // لا نحتاج لكتابة navigation.replace لأن Redux يتولى التوجيه التلقائي الآن
      } else {
        // إذا كان اللاعب جديداً أو مسجل خروج، نوجهه لصفحات المصادقة
        navigation.replace('Auth');
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      navigation.replace('Auth');
    }
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.container}>
        <View style={styles.content}>
          <View style={styles.logoContainer}>
            <Text style={styles.logo}>🎮</Text>
            <Text style={styles.appName}>Rayzi Ultimate</Text>
          </View>
          <Text style={styles.tagline}>منصة الألعاب الشاملة</Text>
        </View>
      </LinearGradient>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  content: {
    alignItems: 'center',
  },
  logoContainer: {
    marginBottom: 20,
    alignItems: 'center',
  },
  logo: {
    fontSize: 80,
  },
  appName: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#fff',
    marginTop: 10,
  },
  tagline: {
    fontSize: 16,
    color: 'rgba(255, 255, 255, 0.9)',
    marginTop: 10,
  },
});

export default SplashScreen;
