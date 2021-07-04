package cn.nukkit.test

import cn.nukkit.utils.ClientChainData

/**
 * An example to show how to use ClientChainData
 * This is also a test for client chain data.
 *
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/6/7 15:07.
 */
@DisplayName("ClientChainData")
internal class ClientChainDataTest {
    @DisplayName("Getters")
    @Test
    @Throws(Exception::class)
    fun testGetter() {
        val `is`: InputStream = ClientChainDataTest::class.java.getResourceAsStream("chain.dat")
        val data: ClientChainData = ClientChainData.of(readStream(`is`))
        val got: String = String.format("userName=%s, clientUUID=%s, " +
                "identityPublicKey=%s, clientId=%d, " +
                "serverAddress=%s, deviceModel=%s, " +
                "deviceOS=%d, gameVersion=%s, " +
                "guiScale=%d, languageCode=%s, " +
                "xuid=%s, currentInputMode=%d, " +
                "defaultInputMode=%d, UIProfile=%d",
                data.getUsername(), data.getClientUUID(),
                data.getIdentityPublicKey(), data.getClientId(),
                data.getServerAddress(), data.getDeviceModel(),
                data.getDeviceOS(), data.getGameVersion(),
                data.getGuiScale(), data.getLanguageCode(),
                data.getXUID(), data.getCurrentInputMode(),
                data.getDefaultInputMode(), data.getUIProfile()
        )
        val expecting = "userName=lmlstarqaq, clientUUID=8323afe1-641e-3b61-9a92-d5d20b279065, " +
                "identityPublicKey=MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE4lyvA1iVhV2u3pLQqJAjJnJZSlSjib8mM1uB5h5yqOBSvCHW+nZxDmkOAW6MS1GA7yGHitGmfS4jW/yUISUdWvLzEWJYOzphb3GNh5J1oLJRwESc5278i4MEDk1y21/q, " +
                "clientId=-6315607246631494544, " +
                "serverAddress=192.168.1.108:19132, deviceModel=iPhone6,2, " +
                "deviceOS=2, gameVersion=1.1.0, " +
                "guiScale=0, languageCode=zh_CN, " +
                "xuid=2535465134455915, currentInputMode=2, " +
                "defaultInputMode=2, UIProfile=1"
        assertEquals(got, expecting)
    }

    companion object {
        @Throws(Exception::class)
        private fun readStream(inStream: InputStream): ByteArray {
            val outSteam = ByteArrayOutputStream()
            val buffer = ByteArray(65536)
            var len: Int
            while (inStream.read(buffer).also { len = it } != -1) {
                outSteam.write(buffer, 0, len)
            }
            outSteam.close()
            inStream.close()
            return outSteam.toByteArray()
        }
    }
}