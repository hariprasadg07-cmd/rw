package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    private const val JARVIS_SYSTEM_INSTRUCTION =
        "You are J.A.R.V.I.S. (Just A Rather Very Intelligent System), the iconic AI assistant originally engineered by Tony Stark. " +
        "You speak with an articulate, polite, calm British butler cadence. Address the user with dignified respect (such as 'Sir', 'Ma'am', or 'Boss'). " +
        "Because your output is spoken aloud via voice text-to-speech, keep your answers concise, witty, and crystal clear. " +
        "Avoid bullet points, asterisks, tables, markdown formatting, or lengthy lists that sound unnatural when spoken. " +
        "Convey high intelligence, subtle humor, and tactical readiness."

    suspend fun askJarvis(userPrompt: String, history: List<Pair<String, String>> = emptyList()): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your GEMINI_API_KEY in AI Studio Secrets panel.")
            )
        }

        val contents = mutableListOf<GeminiContent>()
        
        // Add recent turns for conversational memory (up to 3 pairs)
        history.takeLast(3).forEach { (prevUser, prevJarvis) ->
            contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prevUser))))
            contents.add(GeminiContent(role = "model", parts = listOf(GeminiPart(text = prevJarvis))))
        }
        
        // Add current user prompt
        contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userPrompt))))

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = JARVIS_SYSTEM_INSTRUCTION))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                topP = 0.95f,
                topK = 40,
                maxOutputTokens = 350
            )
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            if (response.error != null) {
                return@withContext Result.failure(Exception(response.error.message ?: "Unknown API error"))
            }

            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!candidateText.isNullOrBlank()) {
                Result.success(candidateText.trim())
            } else {
                Result.failure(Exception("J.A.R.V.I.S. received an empty transmission, Sir."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
