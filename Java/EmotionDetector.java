package com.example.emotionaldiary;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

public class EmotionDetector {

    public interface EmotionCallback {

        void onSuccess(String emotion);

        void onFailure(Exception e);
    }

    private final GenerativeModelFutures model;

    public EmotionDetector() {

        GenerativeModel generativeModel =
                FirebaseAI
                        .getInstance(
                                GenerativeBackend.googleAI()
                        )
                        .generativeModel(
                                "gemini-3.6-flash"
                        );

        model =
                GenerativeModelFutures.from(
                        generativeModel
                );
    }

    public void detectEmotion(
            String diaryContent,
            String previousDiaryContext,
            EmotionCallback callback
    ) {

        String prompt =
                "You are an advanced emotion analysis system "
                        + "for a personal diary application.\n\n"

                        + "Your task is to determine the SINGLE "
                        + "most appropriate emotion for the NEW diary.\n\n"

                        + "Allowed emotions ONLY:\n"
                        + "Happy\n"
                        + "Excited\n"
                        + "Calm\n"
                        + "Neutral\n"
                        + "Sad\n"
                        + "Angry\n\n"

                        + "ANALYSIS RULES:\n"

                        + "1. Analyze the complete meaning of the new diary.\n"

                        + "2. Consider the title and diary content together.\n"

                        + "3. Consider emotional tone, context, events, "
                        + "feelings, intentions and overall situation.\n"

                        + "4. Do NOT classify based only on individual keywords.\n"

                        + "5. The NEW diary is the strongest evidence.\n"

                        + "6. Previous diaries are supporting information only.\n"

                        + "7. Use previous diaries to understand the user's "
                        + "personal writing style and emotional patterns.\n"

                        + "8. Do not blindly copy emotions from previous diaries.\n"

                        + "9. If several emotions appear, select the dominant "
                        + "overall emotion.\n"

                        + "10. If the emotional state is genuinely unclear, "
                        + "choose Neutral.\n"

                        + "11. Never invent a new emotion category.\n"

                        + "12. Return exactly ONE allowed emotion.\n\n"

                        + "====================================\n"
                        + "PREVIOUS DIARY ANALYSIS\n"
                        + "====================================\n\n"

                        + previousDiaryContext

                        + "\n\n"
                        + "====================================\n"
                        + "NEW DIARY\n"
                        + "====================================\n\n"

                        + diaryContent

                        + "\n\n"
                        + "====================================\n"
                        + "FINAL OUTPUT\n"
                        + "====================================\n\n"

                        + "Return ONLY ONE of these exact words:\n"
                        + "Happy\n"
                        + "Excited\n"
                        + "Calm\n"
                        + "Neutral\n"
                        + "Sad\n"
                        + "Angry";

        Content content =
                new Content.Builder()
                        .addText(prompt)
                        .build();

        ListenableFuture<GenerateContentResponse> future =
                model.generateContent(content);

        Futures.addCallback(
                future,
                new FutureCallback<GenerateContentResponse>() {

                    @Override
                    public void onSuccess(
                            GenerateContentResponse response
                    ) {

                        if (response == null) {

                            callback.onFailure(
                                    new Exception(
                                            "AI returned no response."
                                    )
                            );

                            return;
                        }

                        String result =
                                response.getText();

                        if (result == null ||
                                result.trim().isEmpty()) {

                            callback.onFailure(
                                    new Exception(
                                            "AI returned an empty response."
                                    )
                            );

                            return;
                        }

                        String emotion =
                                convertEmotion(result);

                        if (emotion == null) {

                            callback.onFailure(
                                    new Exception(
                                            "AI returned an invalid emotion: "
                                                    + result
                                    )
                            );

                            return;
                        }

                        callback.onSuccess(
                                emotion
                        );
                    }

                    @Override
                    public void onFailure(
                            Throwable throwable
                    ) {

                        throwable.printStackTrace();

                        Exception error;

                        if (throwable instanceof Exception) {

                            error =
                                    (Exception) throwable;

                        } else {

                            error =
                                    new Exception(throwable);
                        }

                        callback.onFailure(error);
                    }
                },
                MoreExecutors.directExecutor()
        );
    }

    private String convertEmotion(
            String result
    ) {

        if (result == null) {
            return null;
        }

        String emotion =
                result
                        .trim()
                        .replace(
                                "\n",
                                " "
                        )
                        .replace(
                                "\r",
                                " "
                        )
                        .trim();

        /*
         * AI sometimes returns:
         *
         * Happy.
         * Happy
         * **Happy**
         * Emotion: Happy
         *
         * Clean those safely.
         */

        emotion =
                emotion
                        .replace(
                                "*",
                                ""
                        )
                        .replace(
                                ".",
                                ""
                        )
                        .trim();

        if (emotion.contains(":")) {

            emotion =
                    emotion
                            .substring(
                                    emotion.lastIndexOf(":") + 1
                            )
                            .trim();
        }

        if (emotion.equalsIgnoreCase("Happy")) {

            return "😊 Happy";
        }

        if (emotion.equalsIgnoreCase("Excited")) {

            return "😍 Excited";
        }

        if (emotion.equalsIgnoreCase("Calm")) {

            return "😌 Calm";
        }

        if (emotion.equalsIgnoreCase("Neutral")) {

            return "😐 Neutral";
        }

        if (emotion.equalsIgnoreCase("Sad")) {

            return "😢 Sad";
        }

        if (emotion.equalsIgnoreCase("Angry")) {

            return "😡 Angry";
        }

        return null;
    }
}