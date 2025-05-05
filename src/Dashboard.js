import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import React from 'react';
import auth from '@react-native-firebase/auth';
import { useNavigation } from '@react-navigation/native';

export default function Dashboard() {
    const navigation = useNavigation();

    const handleLogout = async () => {
        try {
            await auth().signOut();

            navigation.reset({
                index: 0,
                routes: [{ name: 'Login' }],
            });
        }
        catch (error) {
            console.error('Error during logout: ', error);
        }
    }; // Added missing closing brace here

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Dashboard</Text>
            <TouchableOpacity onPress={handleLogout} style={styles.button}>
                <Text style={styles.buttonText}>Logout</Text>
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 20,
    },
    button: {
        backgroundColor: 'red',
        padding: 10,
        borderRadius: 5,
    },
    buttonText: {
        color: 'white',
        fontWeight: 'bold',
    },
});