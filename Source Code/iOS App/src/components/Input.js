import React from 'react';
import {
  View,
  TextInput,
  Text,
  StyleSheet,
  TouchableOpacity,
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';

const Input = ({
  label,
  placeholder,
  value,
  onChangeText,
  secureTextEntry = false,
  keyboardType = 'default',
  autoCapitalize = 'none',
  error,
  icon,
  onIconPress,
  multiline = false,
  numberOfLines = 1,
  style,
  inputStyle,
}) => {
  const [isSecure, setIsSecure] = React.useState(secureTextEntry);

  const toggleSecure = () => {
    setIsSecure(!isSecure);
  };

  return (
    <View style={[styles.container, style]}>
      {label && <Text style={styles.label}>{label}</Text>}
      <View
        style={[
          styles.inputContainer,
          error && styles.inputError,
          multiline && styles.multilineContainer,
        ]}>
        {icon && (
          <Icon
            name={icon}
            size={20}
            color="#888"
            style={styles.leftIcon}
          />
        )}
        <TextInput
          style={[
            styles.input,
            inputStyle,
            icon && styles.inputWithIcon,
            multiline && styles.multilineInput,
          ]}
          placeholder={placeholder}
          placeholderTextColor="#888"
          value={value}
          onChangeText={onChangeText}
          secureTextEntry={isSecure}
          keyboardType={keyboardType}
          autoCapitalize={autoCapitalize}
          multiline={multiline}
          numberOfLines={numberOfLines}
        />
        {secureTextEntry && (
          <TouchableOpacity onPress={toggleSecure} style={styles.rightIcon}>
            <Icon
              name={isSecure ? 'eye-off' : 'eye'}
              size={20}
              color="#888"
            />
          </TouchableOpacity>
        )}
        {onIconPress && !secureTextEntry && (
          <TouchableOpacity onPress={onIconPress} style={styles.rightIcon}>
            <Icon
              name={icon}
              size={20}
              color="#888"
            />
          </TouchableOpacity>
        )}
      </View>
      {error && <Text style={styles.errorText}>{error}</Text>}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    marginBottom: 15,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    paddingHorizontal: 15,
    backgroundColor: '#fff',
  },
  inputError: {
    borderColor: '#e74c3c',
  },
  input: {
    flex: 1,
    paddingVertical: 15,
    fontSize: 16,
    color: '#333',
  },
  inputWithIcon: {
    marginLeft: 10,
  },
  leftIcon: {
    marginRight: 10,
  },
  rightIcon: {
    marginLeft: 10,
    padding: 5,
  },
  multilineContainer: {
    alignItems: 'flex-start',
    paddingTop: 15,
  },
  multilineInput: {
    minHeight: 100,
    textAlignVertical: 'top',
  },
  errorText: {
    fontSize: 12,
    color: '#e74c3c',
    marginTop: 5,
  },
});

export default Input;