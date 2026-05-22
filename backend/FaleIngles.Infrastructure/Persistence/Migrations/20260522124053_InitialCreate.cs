using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace FaleIngles.Infrastructure.Persistence.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "lessons",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    phase = table.Column<int>(type: "integer", nullable: false),
                    order_in_phase = table.Column<int>(type: "integer", nullable: false),
                    title = table.Column<string>(type: "character varying(200)", maxLength: 200, nullable: false),
                    situation_context = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: false),
                    is_premium = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_lessons", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "user_progresses",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    user_id = table.Column<string>(type: "character varying(200)", maxLength: 200, nullable: false),
                    current_streak_days = table.Column<int>(type: "integer", nullable: false),
                    total_minutes_studied = table.Column<int>(type: "integer", nullable: false),
                    today_minutes_studied = table.Column<int>(type: "integer", nullable: false),
                    daily_goal_minutes = table.Column<int>(type: "integer", nullable: false),
                    is_pro = table.Column<bool>(type: "boolean", nullable: false),
                    last_study_date = table.Column<DateOnly>(type: "date", nullable: false),
                    completed_lesson_ids = table.Column<string>(type: "text", nullable: true),
                    mastered_phrase_ids = table.Column<string>(type: "text", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_user_progresses", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "phrases",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uuid", nullable: false),
                    affirmative = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: false),
                    negative = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: true),
                    interrogative = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: true),
                    audio_url = table.Column<string>(type: "character varying(1000)", maxLength: 1000, nullable: true),
                    lesson_id = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_phrases", x => x.id);
                    table.ForeignKey(
                        name: "FK_phrases_lessons_lesson_id",
                        column: x => x.lesson_id,
                        principalTable: "lessons",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "words",
                columns: table => new
                {
                    PhraseId = table.Column<Guid>(type: "uuid", nullable: false),
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    text = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    translation = table.Column<string>(type: "character varying(200)", maxLength: 200, nullable: false),
                    grammatical_type = table.Column<int>(type: "integer", nullable: false),
                    role_in_phrase = table.Column<string>(type: "character varying(300)", maxLength: 300, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_words", x => new { x.PhraseId, x.Id });
                    table.ForeignKey(
                        name: "FK_words_phrases_PhraseId",
                        column: x => x.PhraseId,
                        principalTable: "phrases",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_phrases_lesson_id",
                table: "phrases",
                column: "lesson_id");

            migrationBuilder.CreateIndex(
                name: "IX_user_progresses_user_id",
                table: "user_progresses",
                column: "user_id",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "user_progresses");

            migrationBuilder.DropTable(
                name: "words");

            migrationBuilder.DropTable(
                name: "phrases");

            migrationBuilder.DropTable(
                name: "lessons");
        }
    }
}
